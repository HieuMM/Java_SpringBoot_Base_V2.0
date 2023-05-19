package com.vudt.login.dtos;

import org.springframework.security.core.AuthenticationException;


public class CustomHandleException extends AuthenticationException {
    private int code;

    public CustomHandleException(int code) {
        super(null);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
