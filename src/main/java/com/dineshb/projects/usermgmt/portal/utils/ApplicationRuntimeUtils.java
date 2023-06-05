package com.dineshb.projects.usermgmt.portal.utils;

import com.dineshb.projects.usermgmt.portal.constants.FileConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class ApplicationRuntimeUtils {

    public static void createUserDirectories() {
        log.info("MSG='Creating User directories for the USER_MGMT_PORTAL'");
        new File(FileConstants.USER_FOLDER).mkdirs();
    }
}
