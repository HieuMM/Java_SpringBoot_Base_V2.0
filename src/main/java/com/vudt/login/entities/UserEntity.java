package com.vudt.login.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_users")

public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name", unique = true)
    private String userName;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "password")
    private String password;
    @Column(name = "phone", unique = true)
    private String phone;
    @Column(name = "sex")
    private String sex;
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    public static final String FOLDER = "user/";

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private UserEntity manager;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roles;

    public static String USER_NO_AVATAR = "https://team-2.s3.ap-northeast-2.amazonaws.com/user/no-avatar.png";

    public static boolean hasRole(String roleName, Set<RoleEntity> roleEntities) {
        return roleEntities.stream()
                .anyMatch(x -> x.getRoleName()
                        .equals(roleName));
    }

    public static String getName(UserEntity userEntity) {
        return (userEntity.getFullName() == null || userEntity.getFullName().isEmpty()) ? userEntity.getUserName() : userEntity.getFullName();
    }

}
