package com.dineshb.projects.usermgmt.portal.controller;

import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.dineshb.projects.usermgmt.portal.constants.EndpointConstants.USERS_V1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = USERS_V1)
public class UserController {

    private final UserService userService;

    @GetMapping("/home")
    public String home() {
        return "Welcome to UserManagementPortal 2023 | JB Soft | All Rights Reserved";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        log.info("MSG='Started new User registration'");
        User registeredUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(registeredUser);
    }
}
