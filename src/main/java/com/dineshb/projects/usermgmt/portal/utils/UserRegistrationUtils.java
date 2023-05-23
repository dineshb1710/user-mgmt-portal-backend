package com.dineshb.projects.usermgmt.portal.utils;

import com.dineshb.projects.usermgmt.portal.enums.Role;
import com.dineshb.projects.usermgmt.portal.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.UUID;

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.DEFAULT_USER_IMAGE_PATH;

@Component
@RequiredArgsConstructor
public class UserRegistrationUtils {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    private String getEncodedPassword(final String password) {
        return passwordEncoder.encode(password);
    }

    public User buildUser(String firstName, String lastName, String username, String email, String password) {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(getEncodedPassword(password))
                .email(email)
                .joiningDate(new Date())
                .isLocked(false)
                .isActive(true)
                .role(Role.SIMPLE_USER.name())
                .authorities(Role.SIMPLE_USER.getAuthorities())
                .profileImageUrl(getTemporaryProfileImageUrl())
                .build();
    }

    public Authentication authenticateUser(final String username, final String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
