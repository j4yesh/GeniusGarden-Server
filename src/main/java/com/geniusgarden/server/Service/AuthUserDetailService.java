package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.AuthUser;
import com.geniusgarden.server.Repository.AuthUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthUserDetailService implements UserDetailsService {
    private final AuthUserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AuthUser> authUser = userRepository.findByUsername(username);
        System.out.println("auth userdetaill serviec incst");
        if (!authUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        } else {
            return User.builder()
                    .username(authUser.get().getUsername())
                    .password(authUser.get().getPassword())
                    .disabled(!authUser.get().isActive())
                    .build();
        }
    }
}
