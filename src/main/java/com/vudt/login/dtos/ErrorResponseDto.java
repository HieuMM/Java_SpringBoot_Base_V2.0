package com.vudt.login.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ErrorResponseDto {
    private int code;
    private Object message;

    public static ErrorResponseDto ofError(int code, Object message) {
        return ErrorResponseDto.builder().code(code)
                .message(message).build();
    }
}
