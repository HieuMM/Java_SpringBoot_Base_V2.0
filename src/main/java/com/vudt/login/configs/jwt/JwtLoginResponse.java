package com.vudt.login.configs.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtLoginResponse {
    private Long id;
    private String token;
    private String type;
    private Long timeValid;
    private List<String> authorities;
}
