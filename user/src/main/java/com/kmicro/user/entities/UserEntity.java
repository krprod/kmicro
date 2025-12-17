package com.kmicro.user.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "users")
@Getter @Setter
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  Id;

    @Column(name = "login_name")
    private String loginName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Transient
    private String avtar;

    @Column(name = "is_logged_in")
    private boolean isLoggedIn = false;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_date_time")
    private LocalDateTime lockedDateTime;

    @Column(name = "last_login_time")
    private LocalDateTime lastloginTime;

    @Column(name = "first_login_time")
    private LocalDateTime firstLoginTime;

    @Column(name = "wrong_attempts")
    private int wrongAttempts = 0;

    @Column(name = "latitude")
    private Double latitude = 0.0;

    @Column(name = "longitude")
    private Double longitude = 0.0;

    private Set<String> roles;

    @OneToMany(mappedBy = "user", targetEntity = AddressEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY,   orphanRemoval = true)
    private List<AddressEntity> addresses;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roles == null || this.roles.isEmpty()) {
            return List.of();
        }
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getUsername() {
        return this.email;
    }
//    @Transient
//    @JsonProperty("blackListed")
//    private Boolean blackListed = false;
//    @Transient
//    @JsonProperty("releaseDateTime")
//    private String releasedDateTime;
//    @Transient
//    @JsonProperty("blackListTime")
//    private String blackListTime;
//    @Transient
//    @JsonProperty("blackListReason")
//    private String blackListReason;

}
