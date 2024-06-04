package com.egatrap.partage.common.interceptor;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.security.JwtTokenProvider;
import com.egatrap.partage.service.ChannelPermissionService;
import com.egatrap.partage.service.ChannelService;
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
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelService channelService;
    private final ChannelPermissionService channelPermissionService;

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
                        response.setStatusCode(HttpStatus.BAD_REQUEST);
                        return false;
                    }
                }
            }
        }

        // userId, channelId이 없는 경우 Handshake 중단
        if (userId == null || channelId == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // 채널 권한 조회
        channelRole = channelPermissionService.getChannelRole(channelId, userId);

        // Handshake에 필요한 정보를 attributes에 저장
        attributes.put("userId", userId);
        attributes.put("channelId", channelId);
        attributes.put("channelRole", channelRole); // 채널 권한 정보 저장 커넥션 연결시 or 메세지 전송할때마다확인 중 체크 필요
        log.info("Handshake attributes={}", attributes);

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
