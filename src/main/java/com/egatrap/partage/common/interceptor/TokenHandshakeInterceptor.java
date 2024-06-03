package com.egatrap.partage.common.interceptor;

import com.egatrap.partage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 토큰 검증 로직 추후 암호화 필요
        String token = request.getURI().getQuery(); // Query parameters에서 토큰을 가져옵니다.
        if (token != null && token.startsWith("token=")) {
            token = token.substring(6);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getAuthentication(token).getName();
                log.debug("WS Connection userId: {}", userId);
                attributes.put("userId", userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Handshake 이후 처리 로직
    }
}
