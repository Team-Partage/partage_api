package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorMessageDto {

    private int code;
    private String status;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorMessageDto(HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = httpStatus.name();
    }

    public ErrorMessageDto(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.status = httpStatus.getReasonPhrase();
        this.message = message;
    }
}
