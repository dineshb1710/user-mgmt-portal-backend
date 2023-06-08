package com.dineshb.projects.usermgmt.portal.enums;

import static com.dineshb.projects.usermgmt.portal.constants.Authorities.*;

public enum Role {

    SIMPLE_USER(USER_AUTHORITY),
    HR(HR_AUTHORITY),
    MANAGER(MANAGER_AUTHORITY),
    ADMIN(ADMIN_AUTHORITY),
    SUPER_ADMIN(SUPER_ADMIN_AUTHORITY);

    private String[] authorities;

    Role(String[] authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
