package com.dineshb.projects.usermgmt.portal.utils;

import com.dineshb.projects.usermgmt.portal.enums.Role;
import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.response.HttpResponse;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
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

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.*;
import static com.dineshb.projects.usermgmt.portal.constants.FileConstants.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceUtils {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String getEncodedPassword(final String password) {
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
                .userId(UUID.randomUUID().toString())
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

    public User buildUserForUpdate(User currentUser, String firstName, String lastName, String username, String email, String role,
                                   boolean isLocked, boolean isActive) {

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setRole(getRole(role).name());
        currentUser.setAuthorities(getRole(role).getAuthorities());
        currentUser.setLocked(isLocked);
        currentUser.setActive(isActive);
        currentUser.setProfileImageUrl(getTemporaryProfileImageUrl(username));

        return currentUser;
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

    public HttpResponse buildHttpResponseForUserDeletion(long id) {
        return new HttpResponse(HttpStatus.NO_CONTENT.value(), DELETE_REQUEST,
                getUserDeletedMessage(id), NO_CONTENT, new Date());
    }

    public HttpResponse buildHttpResponseForPasswordReset(String email) {
        return new HttpResponse(HttpStatus.OK.value(), PASSWORD_CHANGE_REQUEST,
                getPasswordResetMessage(email), OK, new Date());
    }

    private String getUserDeletedMessage(long id) {
        return String.format(USER_DELETED_WITH_ID, id);
    }

    private String getPasswordResetMessage(String email) {
        return String.format(PASSWORD_RESET_MAIL_HAS_BEEN_SENT, email);
    }

}
