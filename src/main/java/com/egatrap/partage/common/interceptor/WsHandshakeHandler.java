package com.egatrap.partage.common.interceptor;

import com.egatrap.partage.security.StompPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
public class WsHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        try {
            String userId = attributes.get("userId").toString();
            if("NONE".equals(userId)) {
                return null;
            }
            log.debug("WebSocket Handshake userId : {}", userId);
            return new StompPrincipal(userId);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
