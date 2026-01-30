package com.kmicro.user.security;

import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AccountDeactivated;
import com.kmicro.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceMs implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        UserEntity user = usersRepository.findByEmail(useremail)
                .orElseThrow(() -> new UsernameNotFoundException("User details not found for the user: " + useremail));

        if(!user.isVerified()){
            throw  new AccountDeactivated("User Email Is Not Verified Yet, Check inbox For verification link");
        }

        if(!user.isActive()){
            throw  new AccountDeactivated("User Deactivated the Account on: "
                    + user.getLastloginTime());
        }

        return new User(
                                        user.getEmail(),
                                        user.getPassword(),
                                        user.isEnabled(),
                                        user.isEnabled(),
                                        user.isCredentialsNonExpired(),
                                        user.isAccountNonLocked(),
                                        user.getAuthorities()
                                );

//        List<GrantedAuthority> authorities = user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        // Set user LoggedIn True
//        user.setLoggedIn(Boolean.TRUE);
//        usersRepository.save(user);
//        return User.builder().password(user.getPassword()).username(user.getFirstName()).build();
    }
}//EC
