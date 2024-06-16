package com.egatrap.partage.common.config;

import com.egatrap.partage.common.interceptor.SessionAttributesInterceptor;
import com.egatrap.partage.common.interceptor.WsHandshakeHandler;
import com.egatrap.partage.security.JwtTokenProvider;
import com.egatrap.partage.service.ChannelPermissionService;
import com.egatrap.partage.service.ChannelService;
import com.egatrap.partage.service.ChannelUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${security.cors.allowed-origins}")
    private String ALLOWED_ORIGINS;

    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelService channelService;
    private final ChannelPermissionService channelPermissionService;
    private final ChannelUserService channelUserService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS(); // browser only
        registry.addEndpoint("/ws")
                .setAllowedOrigins(ALLOWED_ORIGINS)
                .setHandshakeHandler(new WsHandshakeHandler())
                .withSockJS()
                .setInterceptors(new SessionAttributesInterceptor(
                        jwtTokenProvider,
                        channelService,
                        channelPermissionService,
                        channelUserService));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/channel");
        config.setApplicationDestinationPrefixes("/stomp");
    }
}
