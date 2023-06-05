package com.dineshb.projects.usermgmt.portal.controller;

import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import com.dineshb.projects.usermgmt.portal.utils.UserServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.dineshb.projects.usermgmt.portal.constants.EndpointConstants.USERS_V1;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.JWT_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = USERS_V1)
public class UserController {

    private final UserService userService;
    private final UserServiceUtils userServiceUtils;

    @GetMapping("/home")
    public String home() {
        return "Welcome to UserManagementPortal 2023 | JB Soft | All Rights Reserved";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        log.info("MSG='Started new User registration'");
        User registeredUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        log.info("MSG='Started User login', username={}", user.getUsername());
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        String jwtToken = userServiceUtils.getJwtToken(new UserPrincipal(loggedInUser));
        HttpHeaders jwtHeaders = new HttpHeaders();
        jwtHeaders.add(JWT_HEADER, jwtToken);
        return new ResponseEntity<>(loggedInUser, jwtHeaders, HttpStatus.ACCEPTED);
    }
}
