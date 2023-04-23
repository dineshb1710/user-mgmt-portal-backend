package com.dineshb.projects.usermgmt.portal.service.impl;

import com.dineshb.projects.usermgmt.portal.enums.Role;
import com.dineshb.projects.usermgmt.portal.exception.CannotRegisterException;
import com.dineshb.projects.usermgmt.portal.exception.EmailExistException;
import com.dineshb.projects.usermgmt.portal.exception.UserExistException;
import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.repo.UserRepository;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import com.dineshb.projects.usermgmt.portal.utils.UserRegistrationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.USER_EMAIL_ALREADY_EXIST;
import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.USER_WITH_USERNAME_ALREADY_EXIST;
import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.CANNOT_REGISTER_USER_DUE_TO_INVALID_INPUT;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRegistrationUtils userRegistrationUtils;

    @Override
    public User register(final String firstName, final String lastName, final String username, final String email) {
        log.info("MSG='Preparing for registration', firstName={}, lastName={}, username={}, email={}", firstName, lastName, username, email);
        if (!validUserInformationProvided(firstName, username, email)) {
            throw new CannotRegisterException(CANNOT_REGISTER_USER_DUE_TO_INVALID_INPUT);
        }
        // Continue with registration..
        return buildAndRegisterUser(firstName, lastName, username, email);
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
    public List<User> getAll() {
        return userRepository.findAll();
    }

    private boolean validUserInformationProvided(final String firstName, final String username, final String email) {
        log.info("MSG='Validating user information for registration', firstName={}, username={}, email={}", firstName, username, email);
        return requiredPropertiesValid(firstName, username, email)
                && findUserByEmail(email) == null
                && findUserByUsername(username) == null;
    }

    private boolean requiredPropertiesValid(final String firstName, final String username, final String email) {
        return !StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(email);
    }

    private User buildAndRegisterUser(final String firstName, final String lastName, final String username, final String email) {
        User newUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .password(userRegistrationUtils.getEncodedPassword())
                .isActive(true)
                .isLocked(false)
                .joiningDate(new Date())
                .role(Role.SIMPLE_USER.name())
                .authorities(Role.SIMPLE_USER.getAuthorities())
                .profileImageUrl(userRegistrationUtils.getTemporaryProfileImageUrl())
                .build();
        return userRepository.save(newUser);
    }
}
