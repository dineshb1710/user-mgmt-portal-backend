package com.dineshb.projects.usermgmt.portal.enums;

import static com.dineshb.projects.usermgmt.portal.constants.Authorities.USER_AUTHORITY;

public enum Role {

    SIMPLE_USER(USER_AUTHORITY),
    HR(USER_AUTHORITY),
    MANAGER(USER_AUTHORITY),
    ADMIN(USER_AUTHORITY),
    SUPER_ADMIN(USER_AUTHORITY);

    private String[] authorities;

    Role(String[] authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
