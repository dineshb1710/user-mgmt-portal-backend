package com.dineshb.projects.usermgmt.portal.service;

import com.dineshb.projects.usermgmt.portal.model.User;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    List<User> getAll();
}
