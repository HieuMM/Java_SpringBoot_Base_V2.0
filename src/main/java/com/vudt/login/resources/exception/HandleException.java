package com.vudt.login.resources.exception;

import com.vudt.login.dtos.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.vudt.login.dtos.CustomHandleException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class HandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> inValidArguments(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        Map<Object, String> errors = new HashMap<>();
        //collect errors
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponseDto.ofError(1, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> resolverException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponseDto.ofError(2, ex.getMessage()));
    }

    @ExceptionHandler(CustomHandleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleError(CustomHandleException ex) {
        ex.printStackTrace();
        return ErrorResponseDto.ofError(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponseDto handleLackPermission(AccessDeniedException ex) {
        ex.printStackTrace();
        return ErrorResponseDto.ofError(5, "You don't have permission to access this resource");
    }
}
