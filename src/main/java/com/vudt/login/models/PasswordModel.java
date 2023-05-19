package com.vudt.login.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordModel {

    @NotNull
    private Long userId;

    @NotNull
    @NotBlank
    private String password;
}
