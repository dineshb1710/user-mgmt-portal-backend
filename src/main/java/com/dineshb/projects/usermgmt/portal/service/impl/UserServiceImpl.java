package com.dineshb.projects.usermgmt.portal.service.impl;

import com.dineshb.projects.usermgmt.portal.exception.*;
import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.repo.UserRepository;
import com.dineshb.projects.usermgmt.portal.service.EmailService;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import com.dineshb.projects.usermgmt.portal.utils.UserServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserServiceUtils userServiceUtils;
    private final EmailService emailService;

    @Override
    public User register(final String firstName, final String lastName, final String username, final String email) {
        log.info("MSG='Preparing for registration', firstName={}, lastName={}, username={}, email={}", firstName, lastName, username, email);
        if (validUserInformationProvided(EMPTY, firstName, lastName, username, email) != null) {
            throw new CannotRegisterException(CANNOT_REGISTER_USER_DUE_TO_INVALID_INPUT);
        }
        // build & register User..
        return buildAndRegisterUser(firstName, lastName, username, email);
    }

    private User buildAndRegisterUser(final String firstName, final String lastName, final String username, final String email) {
        final String password = RandomStringUtils.randomAlphabetic(10);
        User registeredUser = userRepository.save(userServiceUtils.buildUserForRegistration(firstName, lastName, username, email, password));
        log.info("MSG='User registered successfully', firstName={}, lastName={}, username={}, password={}, email={}", firstName, lastName,
                username, password, email);
        emailService.sendUserRegistrationEmail(firstName, lastName, username, email, password);
        return registeredUser;
    }

    private User validUserInformationProvided(final String currentUserName, final String firstName, final String lastName,
                                              final String username, final String email) {
        log.info("MSG='Validating user', currentUserName={}, firstName={}, lastName={}, username={}, email={}", currentUserName, firstName,
                lastName, username, email);
        User userByEmail = findUserByEmail(email);
        User userByUserName = findUserByUsername(username);
        if (!StringUtils.isBlank(currentUserName)) {
            User userByCurrentUserName = findUserByUsername(currentUserName);
            return validateExistingUser(userByCurrentUserName, userByUserName, userByEmail);
        }
        return validateNewUserForRegistration(userByUserName, userByEmail);
    }

    private User validateNewUserForRegistration(final User userByUsername, final User userByEmail) {
        if (userByUsername != null) {
            throw new UserExistException(USER_ALREADY_EXIST);
        }
        if (userByEmail != null) {
            throw new EmailExistException(USER_WITH_EMAIL_ALREADY_EXIST);
        }
        return null;
    }

    private User validateExistingUser(final User userByCurrentUsername, final User userByUserName, final User userByEmail) {

        if (userByCurrentUsername == null) {
            throw new UserNotFoundException(USER_DOES_NT_EXIST);
        }
        if (userByUserName != null && userByUserName.getId() != userByCurrentUsername.getId()) {
            throw new UserExistException(USER_ALREADY_EXIST);
        }
        if (userByEmail != null && userByCurrentUsername.getId() != userByEmail.getId()) {
            throw new EmailNotFoundException(USER_WITH_EMAIL_ALREADY_EXIST);
        }
        return userByCurrentUsername;
    }

    @Override
    public User findUserByUsername(final String username) {
        log.info("MSG='finding user by', username={}", username);
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(final String email) {
        log.info("MSG='finding user by', email={}", email);
        // Check whether the provided user email already exist(s)..
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User login(String username, String password) {
        userServiceUtils.authenticateUser(username, password);
        log.info("MSG='User authentication successful !!'");
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User addUser(String firstName, String lastName, String username, String email, String role, boolean isLocked,
                        boolean isActive, MultipartFile profileImage) throws IOException {
        log.info("MSG='adding new User', firstName={}, lastName={}, username={}, email={}, role={}, isLocked={}, isActive={}, " +
                "profileImage={}", firstName, lastName, username, email, role, isLocked, isActive, profileImage);
        validUserInformationProvided(EMPTY, firstName, lastName, username, email);
        User user = userServiceUtils.buildUserForAddition(firstName, lastName, username, email, role, isLocked, isActive);
        if (profileImage != null) {
            userServiceUtils.updateUserWithProfileImage(user, profileImage);
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(String currentUserName, String newFirstName, String newLastName, String newUsername, String newEmail,
                           String newRole, boolean isLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        log.info("MSG='updating User', currentUserName={}, newFirstName={}, newLastName={}, newUsername={}, newEmail={}, newRole={}, " +
                        "isLocked={}, isActive={}, profileImage={}", currentUserName, newFirstName, newLastName, newUsername, newEmail,
                newRole, isLocked, isActive, profileImage);
        User currentUser = validUserInformationProvided(currentUserName, newFirstName, newLastName, newUsername, newEmail);
        userServiceUtils.buildUserForUpdate(currentUser, newFirstName, newLastName, newUsername, newEmail, newRole,
                isLocked, isActive);
        User updatedUser = userRepository.save(currentUser);
        if (profileImage != null) {
            userServiceUtils.updateUserWithProfileImage(updatedUser, profileImage);
        }
        return updatedUser;
    }

    @Override
    public void deleteUser(long id) {
        log.info("MSG='Deleting user with id', id={}", id);
        Optional<User> userById = userRepository.findById(id);
        if (!userById.isPresent()) {
            throw new UserNotFoundException("User with id=" + id + " doesn't exist !!");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws IOException {
        log.info("MSG='Updating Profile image for user', username={}", username);
        User existingUser = userRepository.findUserByUsername(username);
        if (existingUser == null) {
            throw new UsernameNotFoundException(USERNAME_DOES_NOT_EXIST);
        }
        userServiceUtils.updateUserWithProfileImage(existingUser, profileImage);
        return userRepository.save(existingUser);
    }

    @Override
    public void resetPassword(String email) {
        log.info("MSG='Resetting password for the user', email={}", email);
        User existingUser = userRepository.findUserByEmail(email);
        if (existingUser == null) {
            throw new EmailNotFoundException(USER_WITH_EMAIL_DOES_NT_EXIST);
        }
        String newPassword = RandomStringUtils.randomAlphanumeric(10);
        existingUser.setPassword(userServiceUtils.getEncodedPassword(newPassword));
        userRepository.save(existingUser);
        emailService.sendPasswordResetEmail(existingUser.getFirstName(), existingUser.getLastName(), existingUser.getUsername(),
                existingUser.getEmail(), newPassword);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> {
                    throw new UserNotFoundException(USER_DOES_NT_EXIST);
                }
        );
    }

    @Override
    public List<User> findAll() {
        log.info("MSG='Returning all users from the database', query=/all");
        return userRepository.findAll();
    }
}
