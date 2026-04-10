package ru.ssau.srestapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.srestapp.entity.User;
import ru.ssau.srestapp.exception.EntityType;
import ru.ssau.srestapp.repository.UserRepository;
import ru.ssau.srestapp.security.CustomUserDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(EntityType.USER.notFoundByEmail(email)));
        return toCustomUserDetails(user, extractAuthorities(user));
    }

    @Transactional(readOnly = true)
    public CustomUserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(EntityType.USER.notFound(userId)));
        return toCustomUserDetails(user, extractAuthorities(user));
    }

    private List<SimpleGrantedAuthority> extractAuthorities(User user) {
        if (user.getRole() == null) {
            return List.of();
        }
        String roleName = "ROLE_" + user.getRole().getRoleName().toUpperCase();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    private CustomUserDetails toCustomUserDetails(User user, List<SimpleGrantedAuthority> authorities) {
        return new CustomUserDetails(
                user.getIdUser(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
}
