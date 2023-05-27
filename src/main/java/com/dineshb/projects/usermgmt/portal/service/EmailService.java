package com.dineshb.projects.usermgmt.portal.service;

public interface EmailService {

    void sendUserRegistrationEmail(String firstName, String lastName, String username, String email, String password);
}
