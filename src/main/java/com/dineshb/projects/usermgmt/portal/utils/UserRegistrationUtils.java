package com.dineshb.projects.usermgmt.portal.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.dineshb.projects.usermgmt.portal.constants.ApplicationConstants.DEFAULT_USER_IMAGE_PATH;

@Component
@RequiredArgsConstructor
public class UserRegistrationUtils {

    private final PasswordEncoder passwordEncoder;

    public String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
    }

    public String getEncodedPassword() {
        return passwordEncoder.encode(RandomStringUtils.randomAlphabetic(10));
    }
}
