package com.egatrap.partage.common.interceptor;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.vo.UserSession;
import com.egatrap.partage.security.JwtTokenProvider;
import com.egatrap.partage.service.ChannelPermissionService;
import com.egatrap.partage.service.ChannelService;
import com.egatrap.partage.service.ChannelUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket Handshake Interceptor
 * WebSocket 연결 시 Token 검증을 위한 Interceptor
 */
@Slf4j
@RequiredArgsConstructor
public class SessionAttributesInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelService channelService;
    private final ChannelPermissionService channelPermissionService;
    private final ChannelUserService channelUserService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        // 요청 URL에서 유저 아이디와 채널 아이디 추출
        String query = request.getURI().getQuery();
        String userId = null;
        String channelId = null;
        ChannelRoleType channelRole = null;
        log.debug("WebSocket Handshake Request URL : query={}", query);

        // Query parameters에서 userId, channelId을 가져옴
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue[0].equals("token")) {
                    String token = keyValue[1];
                    userId = jwtTokenProvider.getAuthentication(token).getName();
                } else if (keyValue[0].equals("channel")) {
                    channelId = keyValue[1];
                    // 채널이 존재하지 않는 경우 Handshake 중단
                    if (!channelService.isExistsChannel(channelId)) {
                        log.error("Channel not found : channelId={}", channelId);
                        response.setStatusCode(HttpStatus.BAD_REQUEST);
                        return false;
                    }
                }
            }
        }

        // channelId이 없는 경우 Handshake 중단
        if (channelId == null || channelId.isBlank()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // 비회원인 경우 NONE으로 설정
        if (userId == null || userId.isBlank()) userId = "NONE";

        // 채널 세션 확인 후 없으면 생성
        if(!channelUserService.isExistsChannel(channelId)){
            channelUserService.createChannelSession(channelId);
            log.debug("채널 세션 생성 : channelId={}", channelId);
        }

        // 유저 세션 관리
        UserSession userSession = channelUserService.joinUser(channelId, userId);

        // 세션에 데이터 저장 (userId, channelId, channelRole)
        attributes.put("userSession", userSession);

        log.info("WebSocket init session Data : sessionData={}", userSession);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Handshake 이후 처리 로직
    }
}
