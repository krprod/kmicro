package com.kmicro.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Entity(name = "users")
@Getter @Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  Id;

    private String username;

    @UniqueElements
    private String email;

    private String password;

    private String avtar;

    private boolean isLoggedIn;

    private boolean isLocked;

    @OneToMany(mappedBy = "user", targetEntity = AddressEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,   orphanRemoval = true)
    private List<AddressEntity> addresses;
}
