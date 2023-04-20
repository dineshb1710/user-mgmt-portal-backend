package com.dineshb.projects.usermgmt.portal.controller;

import com.dineshb.projects.usermgmt.portal.exception.EmailNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dineshb.projects.usermgmt.portal.constants.EndpointConstants.USERS_V1;

@RestController
@RequestMapping(value = USERS_V1)
public class UserController {

    @GetMapping("/")
    public String welcome() {
        throw new EmailNotFoundException("No valid emails found !!");
    }
}
