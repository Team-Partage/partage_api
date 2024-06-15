package com.egatrap.partage.common.interceptor;

import com.egatrap.partage.model.vo.SessionAttributes;
import com.egatrap.partage.service.ChannelUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChannelUserService channelUserService;

//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        log.debug("Received a new web socket connection. Session ID: {}", headerAccessor.getSessionId());
//        log.debug("[SessionAttributes] {}", headerAccessor.getSessionAttributes());
//        SessionAttributes session = new SessionAttributes(headerAccessor);
//        channelUserRepository.save(UserSessionEntity.builder()
//                .id(session.getSessionId())
//                .userId(session.getUserId())
//                .channelId(session.getChannelId())
//                .channelRole(session.getChannelRole())
//                .build());
//    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        SessionAttributes session = new SessionAttributes(headerAccessor);
        channelUserService.removeUserFromChannel(session.getSessionId());
        log.debug("Web socket connection closed. Session ID: {}", session.getSessionId());
    }

}
