package com.dineshb.projects.usermgmt.portal.constants;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_00; // 24 Hour(s) in milliseconds..

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Jwt-Header";
    public static final String ISSUER = "EdgeSoft, LLC";
    public static final String AUDIENCE = "User Management Portal";
    public static final String OPTIONS = "Options";
    public static final String AUTHORITIES = "authorities";

    public static final String CANNOT_VERIFY_TOKEN = "Token Cannot be verified !!";
    public static final String TOKEN_EXPIRED = "Token has expired !!";
    public static final String FORBIDDEN_MESSAGE = "You need to login, access forbidden !!";
    public static final String UNAUTHORIZED_MESSAGE = "You do not have access to this resource, Access-Denied !!";

    public static final String[] PUBLIC_URLS = {"/api/v1/user/login", "/api/v1/user/register", "/api/v1/user/forgot/**"};

}
