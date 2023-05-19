package com.vudt.login.repositories;

import com.vudt.login.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findByUserName(String username);

    UserEntity findByEmail(String email);

    UserEntity findByPhone(String phone);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String userName, String userName1);




}
