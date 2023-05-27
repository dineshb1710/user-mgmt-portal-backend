package com.dineshb.projects.usermgmt.portal.service.impl;

import com.dineshb.projects.usermgmt.portal.exception.*;
import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import com.dineshb.projects.usermgmt.portal.repo.UserRepository;
import com.dineshb.projects.usermgmt.portal.service.EmailService;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import com.dineshb.projects.usermgmt.portal.utils.JwtTokenProvider;
import com.dineshb.projects.usermgmt.portal.utils.UserRegistrationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRegistrationUtils userRegistrationUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Override
    public User register(final String firstName, final String lastName,
                         final String username, final String email) {
        log.info("MSG='Preparing for registration', firstName={}, lastName={}, username={}, email={}", firstName, lastName, username, email);
        if (!validUserInformationProvided(StringUtils.EMPTY, firstName, lastName, username, email)) {
            throw new CannotRegisterException(CANNOT_REGISTER_USER_DUE_TO_INVALID_INPUT);
        }
        // build & register User..
        return buildAndRegisterUser(firstName, lastName, username, email);
    }

    private User buildAndRegisterUser(final String firstName, final String lastName,
                                      final String username, final String email) {
        final String password = RandomStringUtils.randomAlphabetic(10);
        User newUser = userRepository.save(userRegistrationUtils.buildUser(firstName, lastName, username, email, password));
        log.info("MSG='User registered successfully', firstName={}, lastName={}, username={}, password={}, email={}", firstName, lastName, username, password, email);
        emailService.sendUserRegistrationEmail(firstName, lastName, username, email, password);
        return newUser;
    }

    private boolean validUserInformationProvided(final String currentUserName, final String firstName, final String lastName, final String username, final String email) {
        log.info("MSG='Validating user', currentUserName={}, firstName={}, lastName={}, username={}, email={}", currentUserName, firstName, lastName, username, email);
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
        userRegistrationUtils.authenticateUser(username, password);
        log.info("MSG='User authentication successful !!'");
        return userRepository.findUserByUsername(username).get();
    }

    @Override
    public String getJwtToken(UserPrincipal userPrincipal) {
        return jwtTokenProvider.generateJwtToken(userPrincipal);
    }
}
