package com.vudt.login.services;

import com.vudt.login.configs.jwt.JwtLoginResponse;
import org.springframework.web.multipart.MultipartFile;

import com.vudt.login.configs.jwt.JwtUserLoginModel;
import com.vudt.login.dtos.UserDto;
import com.vudt.login.entities.UserEntity;
import com.vudt.login.models.PasswordModel;
import com.vudt.login.models.UserModel;
import com.vudt.login.models.UserProfileModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService extends IBaseService<UserEntity, UserDto, UserModel, Long>{

    JwtLoginResponse logIn(JwtUserLoginModel model);

    boolean tokenFilter(String substring, HttpServletRequest req, HttpServletResponse res);

    boolean changeMyAvatar(MultipartFile file);


    boolean updateMyProfile(UserProfileModel model);

    boolean changePassword(String password);


    boolean setPassword(PasswordModel model);


    boolean changeStatus(Long id);

}
