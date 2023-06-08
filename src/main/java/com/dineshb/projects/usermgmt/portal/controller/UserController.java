package com.dineshb.projects.usermgmt.portal.controller;

import com.dineshb.projects.usermgmt.portal.model.User;
import com.dineshb.projects.usermgmt.portal.model.response.HttpResponse;
import com.dineshb.projects.usermgmt.portal.model.security.UserPrincipal;
import com.dineshb.projects.usermgmt.portal.service.UserService;
import com.dineshb.projects.usermgmt.portal.utils.UserServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.dineshb.projects.usermgmt.portal.constants.EndpointConstants.USERS_V1;
import static com.dineshb.projects.usermgmt.portal.constants.FileConstants.*;
import static com.dineshb.projects.usermgmt.portal.constants.SecurityConstants.JWT_HEADER;
import static java.lang.Boolean.parseBoolean;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

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
    public ResponseEntity<User> registerUser(@RequestBody final User user) {
        log.info("MSG='Started new User registration'");
        User registeredUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(registeredUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody final User user) {
        log.info("MSG='Started User login', username={}", user.getUsername());
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        String jwtToken = userServiceUtils.getJwtToken(new UserPrincipal(loggedInUser));
        HttpHeaders jwtHeaders = new HttpHeaders();
        jwtHeaders.add(JWT_HEADER, jwtToken);
        return new ResponseEntity<>(loggedInUser, jwtHeaders, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestParam("firstName") final String firstName,
                                        @RequestParam("lastName") final String lastName,
                                        @RequestParam("username") final String username,
                                        @RequestParam("email") final String email,
                                        @RequestParam("role") final String role,
                                        @RequestParam("isLocked") final String isLocked,
                                        @RequestParam("isActive") final String isActive,
                                        @RequestParam(value = "profileImage", required = false) final MultipartFile profileImage) throws IOException {
        User addedUser = userService.addUser(firstName, lastName, username, email, role, parseBoolean(isLocked),
                parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(addedUser, OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam("currentUserName") final String currentUserName,
                                           @RequestParam("firstName") final String firstName,
                                           @RequestParam("lastName") final String lastName,
                                           @RequestParam("username") final String username,
                                           @RequestParam("email") final String email,
                                           @RequestParam("role") final String role,
                                           @RequestParam("isLocked") final String isLocked,
                                           @RequestParam("isActive") final String isActive,
                                           @RequestParam(value = "profileImage", required = false) final MultipartFile profileImage) throws IOException {
        User updatedUser = userService.updateUser(currentUserName, firstName, lastName, username, email, role,
                parseBoolean(isLocked), parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{userName}")
    public ResponseEntity<User> findByUserName(@PathVariable("userName") final String userName) {
        User userByUserName = userService.findUserByUsername(userName);
        return new ResponseEntity<>(userByUserName, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteById(@PathVariable("id") final Long id) {
        log.info("MSG='Deleting User', id={}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(userServiceUtils.buildHttpResponseForUserDeletion(id));
    }

    @PutMapping("/update/profileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") final String userName,
                                                   @RequestParam("profileImage") final MultipartFile profileImage) throws IOException {
        User updatedUser = userService.updateProfileImage(userName, profileImage);
        return new ResponseEntity(updatedUser, OK);
    }

    @PostMapping("/reset/password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestParam("email") final String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok(userServiceUtils.buildHttpResponseForPasswordReset(email));
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping(value = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") final String username,
                                  @PathVariable("fileName") final String fileName) {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return bytes;
    }

    @GetMapping(value = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") final String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            byte[] chunk = new byte[CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
