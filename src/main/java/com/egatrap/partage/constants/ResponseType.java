package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseType {

    SUCCESS("Success", true),
    FAIL("Fail", false),
    ERROR("Error", false);

    private final String message;
    private final boolean status;

}
