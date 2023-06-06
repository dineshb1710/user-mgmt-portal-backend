package com.dineshb.projects.usermgmt.portal.utils;

import com.dineshb.projects.usermgmt.portal.enums.Role;
import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static com.dineshb.projects.usermgmt.portal.constants.FileConstants.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceUtils {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private String getEncodedPassword(final String password) {
        return passwordEncoder.encode(password);
    }

    public User buildUserForRegistration(String firstName, String lastName, String username, String email, String password) {
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
                .profileImageUrl(getTemporaryProfileImageUrl(username))
                .build();
    }

    public Authentication authenticateUser(final String username, final String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public String getJwtToken(UserPrincipal userPrincipal) {
        return jwtTokenProvider.generateJwtToken(userPrincipal);
    }

    public User buildUserForAddition(String firstName, String lastName, String username, String email, String role,
                                     boolean isLocked, boolean isActive) {
        final String password = RandomStringUtils.randomAlphabetic(10);
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(getRole(role).name())
                .authorities(getRole(role).getAuthorities())
                .joiningDate(new Date())
                .isLocked(isLocked)
                .isActive(isActive)
                .profileImageUrl(getTemporaryProfileImageUrl(username))
                .build();
    }

    public User buildUserForUpdate(String firstName, String lastName, String username, String email, String role,
                                   boolean isLocked, boolean isActive) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .role(getRole(role).name())
                .authorities(getRole(role).getAuthorities())
                .isLocked(isLocked)
                .isActive(isActive)
                .profileImageUrl(getTemporaryProfileImageUrl(username))
                .build();
    }

    private Role getRole(String role) {
        return Role.valueOf(role);
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(TEMP_USER_IMAGE_PATH + username).toUriString();
    }

    public User updateUserWithProfileImage(User user, MultipartFile profileImage) throws IOException {
        log.info("MSG='Saving User profileImage', username={}", user.getUsername());
        Path userPath = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        if (!Files.exists(userPath)) {
            log.info("MSG='No userPath exist for this user for profile', username={}", user.getUsername());
            Files.createDirectories(userPath);
            log.info("MSG='Required directories created !!'");
        }
        Files.deleteIfExists(Paths.get(userPath + user.getUsername() + DOT + JPG_EXTENSION));
        Files.copy(profileImage.getInputStream(), userPath.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
        user.setProfileImageUrl(buildProfileImageUrl(user.getUsername()));
        return user;
    }

    private String buildProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH +
                username + DOT + JPG_EXTENSION).toUriString();
    }
}
