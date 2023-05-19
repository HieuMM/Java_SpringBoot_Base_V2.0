package com.vudt.login.dtos;

import com.vudt.login.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String avatar;
    private String phone;
    private String sex;
    private Date birthDate;
    private boolean status;
    private String address;
    private Date createdDate;
    private Date updatedDate;
    private List<RoleDto> roles;

    public static UserDto toDto(UserEntity userEntity) {
        if (userEntity == null) return null;
        return UserDto.builder()
                .id(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .sex(userEntity.getSex())
                .fullName(userEntity.getFullName())
                .avatar(userEntity.getAvatar() == null ? UserEntity.USER_NO_AVATAR : userEntity.getAvatar())
                .birthDate(userEntity.getBirthDate())
                .status(userEntity.getStatus())
                .createdDate(userEntity.getCreatedDate())
                .updatedDate(userEntity.getUpdatedDate())
                .address(userEntity.getAddress())
                .roles(userEntity.getRoles()
                        .stream()
                        .map(r -> RoleDto.builder()
                                .roleId(r.getRoleId())
                                .roleName(r.getRoleName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}

