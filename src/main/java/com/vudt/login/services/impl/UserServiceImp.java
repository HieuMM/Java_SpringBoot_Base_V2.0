package com.vudt.login.services.impl;


import com.vudt.login.configs.jwt.JwtLoginResponse;
import com.vudt.login.configs.jwt.JwtProvider;
import com.vudt.login.configs.jwt.JwtUserLoginModel;
import com.vudt.login.dtos.CustomHandleException;
import com.vudt.login.dtos.UserDto;
import com.vudt.login.entities.RoleEntity;
import com.vudt.login.entities.UserEntity;
import com.vudt.login.models.PasswordModel;
import com.vudt.login.models.UserModel;
import com.vudt.login.models.UserProfileModel;
import com.vudt.login.repositories.IRoleRepository;
import com.vudt.login.repositories.IUserRepository;
import com.vudt.login.services.CustomUserDetail;
import com.vudt.login.services.IUserService;
import com.vudt.login.services.MailService;
import com.vudt.login.utils.FileUploadProvider;
import com.vudt.login.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Order(5)
@Transactional
public class UserServiceImp implements IUserService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileUploadProvider fileUploadProvider;

    @Value("${timeKeepingDate}")
    int timeKeepingDate;


    public UserServiceImp(IUserRepository userRepository,
                          IRoleRepository roleRepository,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          MailService mailService, FileUploadProvider fileUploadProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.fileUploadProvider = fileUploadProvider;


        // create default roles
        try {
            // for insert default roles
            if (this.roleRepository.findAllByRoleIdIn(List.of(1L, 2L)).size() < 2) {
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(1L).roleName(RoleEntity.ADMIN).build());
                this.roleRepository.saveAndFlush(RoleEntity.builder().roleId(2L).roleName(RoleEntity.USER).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            // for insert default admin
            if (this.userRepository.findById(1L).isEmpty()) {
                UserEntity administrator = UserEntity.builder()
                        .userId(1L)
                        .fullName("administrator")
                        .status(true)
                        .userName("admin")
                        .password(this.passwordEncoder.encode("123456"))
                        .build();

                Set<UserEntity> users = Set.of(administrator);
                administrator.setRoles(Set.of(
                        RoleEntity.builder().roleId(1L).roleName(RoleEntity.ADMIN).build()
                ));

                this.userRepository.save(administrator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<UserDto> findAll() {
        List<UserEntity> userEntities = this.userRepository.findAll();
        return userEntities.stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> findAll(Pageable page) {
        logger.info("{} is finding all users", SecurityUtils.getCurrentUsername());
        return this.userRepository.findAll(page).map(UserDto::toDto);
    }

    @Override
    public List<UserDto> findAll(Specification<UserEntity> specs) {
        logger.info("{} is finding all users", SecurityUtils.getCurrentUsername());
        return this.userRepository.findAll(specs).stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> filter(Pageable page, Specification<UserEntity> specs) {
        return this.userRepository.findAll(specs, page).map(UserDto::toDto);
    }

    @Override
    public UserDto findById(Long id) {
        logger.info("{} finding user id: {}", SecurityUtils.getCurrentUsername(), id);
        return UserDto.toDto(this.getById(id));
    }

    @Override
    public UserEntity getById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new CustomHandleException(11));
    }

    @Override
    public UserDto add(UserModel model) {
        logger.info("{} is adding user", SecurityUtils.getCurrentUsername());
        // check user has existed with email
        UserEntity checkUser = this.userRepository.findByEmail(model.getEmail());
        if (checkUser != null)
            throw new CustomHandleException(12);

        // check user has existed with username
        checkUser = this.userRepository.findByUserName(model.getUserName());
        if (checkUser != null)
            throw new CustomHandleException(13);

        // check user has existed with phone
        if (model.getPhone() != null) {
            checkUser = this.userRepository.findByPhone(model.getPhone());
            if (checkUser != null)
                throw new CustomHandleException(14);
        }

        if (model.getPassword() == null || model.getPassword().isEmpty()) {
            throw new CustomHandleException(16);
        }


        UserEntity userEntity = UserModel.toEntity(model);

        userEntity.setStatus(true);
        userEntity.setPassword(this.passwordEncoder.encode(model.getPassword()));
        this.setRoles(userEntity, model.getRoles());
        return UserDto.toDto(this.userRepository.saveAndFlush(userEntity));
    }

    @Override
    public List<UserDto> add(List<UserModel> model) {
        return null;
    }

    @Override
    public UserDto update(UserModel model) {
        if (model.getId().equals(1L))
            throw new CustomHandleException(19);
        logger.info("{} is updating user id: {}", SecurityUtils.getCurrentUsername(), model.getId());

        UserEntity original = this.getById(model.getId());

        // check user has existed if user update their email
        if (!model.getEmail().equals(original.getEmail())) {
            UserEntity checkUser = this.userRepository.findByEmail(model.getEmail());
            if (checkUser != null && !checkUser.getUserId().equals(original.getUserId()))
                throw new CustomHandleException(12);
        }

        // check user has existed if user update their phone
        if (!model.getPhone().equals(original.getPhone())) {
            UserEntity checkUser = this.userRepository.findByPhone(model.getPhone());
            if (checkUser != null && !checkUser.getUserId().equals(original.getUserId()))
                throw new CustomHandleException(14);
        }


        if (model.getPassword() != null) {
            if (model.getPassword().isEmpty())
                throw new CustomHandleException(17);
            else
                original.setPassword(this.passwordEncoder.encode(model.getPassword()));
        }
        original.setEmail(model.getEmail());
        original.setBirthDate(model.getBirthDate());
        original.setFullName(model.getFullName());
        original.setSex(model.getSex());
        original.setPhone(model.getPhone());
        original.setAddress(model.getAddress());
        this.setRoles(original, model.getRoles());
        return UserDto.toDto(this.userRepository.saveAndFlush(original));
    }

    public void setRoles(UserEntity user, List<Long> roles) {
        if (roles == null || roles.isEmpty())
            user.setRoles(Collections.singleton(this.roleRepository.findByRoleName(RoleEntity.USER)));
        else
            user.setRoles(this.roleRepository.findAllByRoleIdIn(roles));
    }

    @Override
    public boolean deleteById(Long id) {
        if (id.equals(1L))
            throw new CustomHandleException(18);
        logger.info("{} is deleting user id: {}", SecurityUtils.getCurrentUsername(), id);
        UserEntity userEntity = this.getById(id);
        userEntity.setStatus(false);
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
        return true;
    }

    @Override
    public JwtLoginResponse logIn(JwtUserLoginModel userLogin) {
        UserEntity user = this.findByUsername(userLogin.getUsername());
        UserDetails userDetail = new CustomUserDetail(user);
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetail, userLogin.getPassword(), userDetail.getAuthorities()));
        } catch (Exception e) {
            throw e;
        }

        long timeValid = userLogin.isRemember() ? 86400 * 7 : 1800L;
        return JwtLoginResponse.builder()
                .id(user.getUserId())
                .token(this.jwtProvider.generateToken(userDetail.getUsername(), timeValid))
                .type("Bearer").authorities(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .timeValid(timeValid)
                .build();
    }

    public UserEntity findByUsername(String userName) {
        return userRepository.findUserEntityByUserNameOrEmail(userName, userName).orElseThrow(() -> new CustomHandleException(11));
    }

    // Token filter, check token is valid and set to context
    @Transactional
    public boolean tokenFilter(String token, HttpServletRequest req, HttpServletResponse res) {
        try {
            String username = this.jwtProvider.getUsernameFromToken(token);
            CustomUserDetail userDetail = new CustomUserDetail(this.findByUsername(username));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            req.getSession().setAttribute("object", usernamePasswordAuthenticationToken.getPrincipal());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeMyAvatar(MultipartFile file) {
        logger.info("{} is updating avatar", SecurityUtils.getCurrentUsername());

        UserEntity userEntity = this.getById(SecurityUtils.getCurrentUserId());
        try {
            String folder = "users" + userEntity.getUserName() + "/";
            userEntity.setAvatar(this.fileUploadProvider.uploadFile(folder, file));
        } catch (IOException e) {
            throw new CustomHandleException(15);
        }
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }


    @Override
    public boolean updateMyProfile(UserProfileModel model) {
        UserEntity userEntity = this.getById(SecurityUtils.getCurrentUserId());
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        this.checkUserInfoDuplicate(userEntity, model.getEmail(), model.getPhone());
        userEntity.setFullName(model.getFullName());
        userEntity.setBirthDate(model.getBirthDate());
        userEntity.setSex(model.getSex());
        userEntity.setAddress(model.getAddress());
        userEntity.setPhone(model.getPhone());
        userEntity.setEmail(model.getEmail());
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    private void checkUserInfoDuplicate(UserEntity userEntity, String email, String phone) {
        // check user has existed if user update their email
        if (email != null)
            if (!email.equals(userEntity.getEmail())) {
                UserEntity checkUser = this.userRepository.findByEmail(phone);
                if (checkUser != null && !checkUser.getUserId().equals(userEntity.getUserId()))
                    throw new CustomHandleException(12);
            }

        // check user has existed if user update their phone
        if (phone != null)
            if (!phone.equals(userEntity.getPhone())) {
                UserEntity checkUser = this.userRepository.findByPhone(phone);
                if (checkUser != null && !checkUser.getUserId().equals(userEntity.getUserId()))
                    throw new CustomHandleException(14);
            }

    }

    @Override
    public boolean changePassword(String password) {
        logger.info("{} is changing password", SecurityUtils.getCurrentUsername());
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setPassword(this.passwordEncoder.encode(password));
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean setPassword(PasswordModel model) {
        logger.info("{} is setting password for user id: {}", SecurityUtils.getCurrentUsername(), model.getUserId());
        UserEntity userEntity = this.getById(model.getUserId());
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setPassword(this.passwordEncoder.encode(model.getPassword()));
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }

    @Override
    public boolean changeStatus(Long id) {
        logger.info("{} is changing status", SecurityUtils.getCurrentUsername());
        UserEntity userEntity = this.getById(id);
        if (userEntity.getUserId().equals(1L))
            throw new CustomHandleException(19);
        userEntity.setStatus(!userEntity.getStatus());
        this.userRepository.saveAndFlush(userEntity);
        return true;
    }


}
