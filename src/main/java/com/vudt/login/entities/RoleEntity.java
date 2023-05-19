package com.vudt.login.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbl_roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {
    public final static String ADMIN = "ROLE_ADMIN";
    public final static String USER = "ROLE_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", unique = true)
    private String roleName;

}
