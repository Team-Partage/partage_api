package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ResponseType;

public class ResponseJoinDto extends ResponseDto {

    public ResponseJoinDto(String code, String message, boolean status) {
        super(code, message, status);
    }

    public ResponseJoinDto(ResponseType type) {
        super(type);
    }
}
