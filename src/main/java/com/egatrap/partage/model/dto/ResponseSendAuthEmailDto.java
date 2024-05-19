package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ResponseType;
import org.springframework.http.HttpStatus;

public class ResponseSendAuthEmailDto extends ResponseDto {

    public ResponseSendAuthEmailDto(String code, String message, boolean status) {
        super(code, message, status);
    }

    public ResponseSendAuthEmailDto(ResponseType type) {
        super(type);
    }
}
