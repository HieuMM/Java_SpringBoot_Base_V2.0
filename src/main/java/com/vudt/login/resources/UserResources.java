package com.vudt.login.resources;

import com.vudt.login.configs.FrontendConfiguration;
import com.vudt.login.services.IUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.vudt.login.configs.jwt.JwtLoginResponse;
import com.vudt.login.configs.jwt.JwtUserLoginModel;
import com.vudt.login.dtos.UserDto;
import com.vudt.login.entities.RoleEntity;
import com.vudt.login.models.UserProfileModel;
import com.vudt.login.utils.SecurityUtils;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;


@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "users")
public class UserResources {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final IUserService userService;

    public UserResources(IUserService userService) {
        this.userService = userService;
    }

    @ApiOperation(notes = "get user by id", authorizations = {@Authorization(value = "Bearer")}, value = "Login")
    @RolesAllowed({RoleEntity.ADMIN})
    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return this.userService.findById(id);
    }

    @PostMapping("login")
    public JwtLoginResponse loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        log.info("{} is logging in system", model.getUsername());
        return this.userService.logIn(model);
    }

    @RolesAllowed({RoleEntity.ADMIN})
    @PatchMapping("change_status/{id}")
    public boolean changeStatus(@PathVariable Long id) {
        return this.userService.changeStatus(id);
    }

    @GetMapping("my_profile")
    public UserDto getMyProfile() {
        return this.userService.findById(SecurityUtils.getCurrentUserId());
    }

    @PostMapping("change_password")
    public boolean changePassword(@RequestBody String password) {
        return this.userService.changePassword(password);
    }

    @PatchMapping("update_my_profile")
    public boolean updateMyProfile(@RequestBody UserProfileModel model) {
        return this.userService.updateMyProfile(model);
    }

    @PatchMapping("change_my_avatar")
    public boolean changeMyAvatar(MultipartFile file) {
        return this.userService.changeMyAvatar(file);
    }

}
