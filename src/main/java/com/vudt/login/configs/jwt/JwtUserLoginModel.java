package com.vudt.login.configs.jwt;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserLoginModel {  //Authentication login model
    @ApiModelProperty(notes = "User name", dataType = "String", example = "admin")
    @NotNull
    @NotBlank
    private String username;
    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    @NotNull
    @NotBlank
    private String password;
    @ApiModelProperty(notes = "Remember Me", dataType = "Boolean", example = "true")
    private boolean remember;
}
