package com.dineshb.projects.usermgmt.portal.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

import static com.dineshb.projects.usermgmt.portal.constants.FileConstants.USER_FOLDER;

@Slf4j
public class ApplicationRuntimeUtils {

    public static void createUserDirectories() {
        log.info("MSG='Creating User directories for the USER_MGMT_PORTAL'");
        new File(USER_FOLDER).mkdirs();
    }
}
