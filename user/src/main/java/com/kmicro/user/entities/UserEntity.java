package com.kmicro.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "users")
@Getter @Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long  Id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String password;

    @OneToMany(mappedBy = "user", targetEntity = AddressEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,   orphanRemoval = true)
    private List<AddressEntity> addresses;
}
