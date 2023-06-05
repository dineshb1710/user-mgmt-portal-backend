package com.dineshb.projects.usermgmt.portal.service;

import com.dineshb.projects.usermgmt.portal.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    User register(String firstName, String lastName, String username, String email);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User login(String username, String password);

    User addUser(String firstName, String lastName, String username, String email, String role, boolean isLocked,
                 boolean isActive, MultipartFile profileImage) throws IOException;

    User updateUser(String currentUserName, String firstName, String lastName, String username, String email, String role, boolean isLocked,
                    boolean isActive, MultipartFile profileImage) throws IOException;

    void deleteUser(long id);

    User updateProfileImage(String username, MultipartFile profileImage) throws IOException;

    void resetPassword(String email);
}
