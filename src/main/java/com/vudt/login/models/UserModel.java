package com.vudt.login.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vudt.login.entities.UserEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    @ApiModelProperty(notes = "User ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "User Email", dataType = "String", example = "email@gmail.com")
    @Email
    @NotNull
    private String email;

    @ApiModelProperty(notes = "sex", dataType = "String", example = "1 in (nam, nữ, khác)")
    private String sex;

    @ApiModelProperty(notes = "Username", dataType = "String", example = "user")
    @NotNull
    private String userName;

    @ApiModelProperty(notes = "User full name", dataType = "String", example = "Nguyen Van A")
    @NotNull
    private String fullName;

    @ApiModelProperty(notes = "User password", dataType = "String", example = "123456")
    private String password;

    @ApiModelProperty(notes = "User birthdate", dataType = "Date", example = "2002-04-29")
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    @ApiModelProperty(notes = "User roles", dataType = "array", example = "[1,3,5]")
    @NotNull
    private List<Long> roles;

    @ApiModelProperty(notes = "phone, phone must format follow vietnam", dataType = "String", example = "0952888888")
    private String phone;

    @ApiModelProperty(notes = "address", dataType = "String", example = "my dinh")
    private String address;


    public static UserEntity toEntity(UserModel model) {
        if (model == null) throw new RuntimeException("UserModel is null");
        return UserEntity.builder()
                .userName(model.userName)
                .fullName(model.getFullName())
                .email(model.getEmail())
                .password(model.getPassword())
                .birthDate(model.getBirthDate())
                .address(model.address)
                .sex(model.sex)
                .phone(model.getPhone())
                .userId(model.getId()).build();
    }
}
