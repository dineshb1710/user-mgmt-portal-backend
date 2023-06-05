package com.dineshb.projects.usermgmt.portal;

import com.dineshb.projects.usermgmt.portal.utils.ApplicationRuntimeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserMgmtPortalBackendApplication {


    public static void main(String[] args) {
        SpringApplication.run(UserMgmtPortalBackendApplication.class, args);
        ApplicationRuntimeUtils.createUserDirectories();
    }

}
