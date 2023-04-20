package com.dineshb.projects.usermgmt.portal.exception;

public class EmailExistException extends RuntimeException {

    public EmailExistException(String message) {
        super(message);
    }
}
