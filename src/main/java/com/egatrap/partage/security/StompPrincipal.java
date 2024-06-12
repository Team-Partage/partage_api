package com.egatrap.partage.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@Data
@AllArgsConstructor
public class StompPrincipal implements Principal {
    private String name;
}
