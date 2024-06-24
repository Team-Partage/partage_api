package com.egatrap.partage.exception;

import lombok.Getter;

public class SendMessageException extends RuntimeException {

    @Getter
    private final String channelId;

    public SendMessageException(String channelId, String message) {
        super(message);
        this.channelId = channelId;
    }

    public SendMessageException(String message, Throwable cause, String channelId) {
        super(message, cause);
        this.channelId = channelId;
    }

    public SendMessageException(Throwable cause, String channelId) {
        super(cause);
        this.channelId = channelId;
    }

}
