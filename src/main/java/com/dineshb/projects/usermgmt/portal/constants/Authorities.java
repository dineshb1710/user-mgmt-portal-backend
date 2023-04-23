package com.dineshb.projects.usermgmt.portal.constants;

public class Authorities {

    public static final String[] USER_AUTHORITY = {"user:read"};
    public static final String[] HR_AUTHORITY = {"user:read", "user:update"};
    public static final String[] MANAGER_AUTHORITY = {"user:read", "user:update"};
    public static final String[] ADMIN_AUTHORITY = {"user:create", "user:read", "user:update"};
    public static final String[] SUPER_ADMIN_AUTHORITY = {"user:create", "user:read", "user:update", "user:delete"};
}
