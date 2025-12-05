package com.kmicro.user.security;

import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserDetailServiceMs implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        UserEntity user = usersRepository.findByEmail(useremail)
                .orElseThrow(() -> new UsernameNotFoundException("User details not found for the user: " + useremail));

//        List<GrantedAuthority> authorities = user.getAuthorities().stream().map(authority -> new
//                SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
//        return User.builder().password(user.getPassword()).username(user.getFirstName()).build();
        return new User(user.getEmail(),user.getPassword(), Arrays.asList(() -> "USER"));
    }
}
