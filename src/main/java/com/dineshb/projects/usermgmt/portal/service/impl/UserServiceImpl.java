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
        if (!validUserInformationProvided(EMPTY, firstName, lastName, username, email)) {
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

    private boolean validUserInformationProvided(final String currentUserName, final String firstName, final String lastName,
                                                 final String username, final String email) {
        log.info("MSG='Validating user', currentUserName={}, firstName={}, lastName={}, username={}, email={}", currentUserName, firstName,
                lastName, username, email);
        if (!StringUtils.isBlank(currentUserName)) {
            return validateExistingUser(currentUserName, email);
        }
        return validateNewUserForRegistration(username, email);
    }

    private boolean validateNewUserForRegistration(final String newUserName, final String email) {
        if (findUserByUsername(newUserName) != null) {
            throw new UserExistException(USER_ALREADY_EXIST);
        }
        if (findUserByEmail(email) != null) {
            throw new EmailExistException(USER_WITH_EMAIL_ALREADY_EXIST);
        }
        return true;
    }

    private boolean validateExistingUser(final String currentUserName, final String email) {
        if (findUserByUsername(currentUserName) == null) {
            throw new UserNotFoundException(USER_DOES_NT_EXIST);
        }
        if (findUserByEmail(email) == null) {
            throw new EmailNotFoundException(USER_WITH_EMAIL_DOES_NT_EXIST);
        }
        return true;
    }

    @Override
    public User findUserByUsername(final String username) {
        log.info("MSG='finding user by', username={}", username);
        // Check whether the provided username already exist(s)..
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            throw new UserExistException(USER_WITH_USERNAME_ALREADY_EXIST);
        }
        return null;
    }

    @Override
    public User findUserByEmail(final String email) {
        log.info("MSG='finding user by', email={}", email);
        // Check whether the provided user email already exist(s)..
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            throw new EmailExistException(USER_EMAIL_ALREADY_EXIST);
        }
        return null;
    }

    @Override
    public User login(String username, String password) {
        userServiceUtils.authenticateUser(username, password);
        log.info("MSG='User authentication successful !!'");
        return userRepository.findUserByUsername(username).get();
    }

    @Override
    public User addUser(String firstName, String lastName, String username, String email, String role, boolean isLocked,
                        boolean isActive, MultipartFile profileImage) throws IOException {
        log.info("MSG='adding new User', firstName={}, lastName={}, username={}, email={}, role={}, isLocked={}, isActive={}, " +
                "profileImage={}", firstName, lastName, username, email, role, isLocked, isActive, profileImage);
        validUserInformationProvided(EMPTY, firstName, lastName, username, email);
        User user = userServiceUtils.buildUserForAddition(firstName, lastName, username, email, role, isLocked, isActive);
        User savedUser = userServiceUtils.updateUserWithProfileImage(user, profileImage);
        return userRepository.save(savedUser);
    }

    @Override
    public User updateUser(String currentUserName, String newFirstName, String newLastName, String newUsername, String newEmail,
                           String newRole, boolean isLocked, boolean isActive, MultipartFile profileImage) throws IOException {
        log.info("MSG='updating User', currentUserName={}, newFirstName={}, newLastName={}, newUsername={}, newEmail={}, newRole={}, " +
                        "isLocked={}, isActive={}, profileImage={}", currentUserName, newFirstName, newLastName, newUsername, newEmail,
                newRole, isLocked, isActive, profileImage);
        validUserInformationProvided(currentUserName, newFirstName, newLastName, newUsername, newEmail);
        User user = userServiceUtils.buildUserForUpdate(newFirstName, newLastName, newUsername, newEmail, newRole,
                isLocked, isActive);
        User updatedUser = userRepository.save(user);
        userServiceUtils.updateUserWithProfileImage(updatedUser, profileImage);
        return updatedUser;
    }

    @Override
    public void deleteUser(long id) {
        log.info("MSG='Deleting user with id', id={}", id);
        userRepository.deleteById(id);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws IOException {
        log.info("MSG='Updating Profile image for user', username={}", username);
        Optional<User> user = userRepository.findUserByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(USERNAME_DOES_NOT_EXIST);
        }
        User existingUser = user.get();
        userServiceUtils.updateUserWithProfileImage(existingUser, profileImage);
        return userRepository.save(existingUser);
    }

    @Override
    public void resetPassword(String email) {
        log.info("MSG='Resetting password for the user', email={}", email);
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new EmailNotFoundException(USER_WITH_EMAIL_DOES_NT_EXIST);
        }
        User existingUser = optionalUser.get();
        existingUser.setPassword(RandomStringUtils.randomAlphanumeric(10));
        userRepository.save(existingUser);
        emailService.sendPasswordResetEmail(existingUser.getFirstName(), existingUser.getLastName(), existingUser.getUsername(),
                existingUser.getEmail(), existingUser.getPassword());
    }

}
