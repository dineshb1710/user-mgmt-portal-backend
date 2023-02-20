package com.dineshb.projects.usermgmt.portal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dineshb.projects.usermgmt.portal.constants.EndpointConstants.USERS_V1;

@RestController
@RequestMapping(value = USERS_V1)
public class UserController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome to User Management Portal !!";
    }
}
