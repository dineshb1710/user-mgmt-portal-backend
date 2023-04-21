package com.dineshb.projects.usermgmt.portal.controller;

import com.dineshb.projects.usermgmt.portal.exception.HandlerNotFoundException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dineshb.projects.usermgmt.portal.constants.AdviceConstants.NO_VALID_MAPPING_FOUND;

@RestController
public class InvalidMappingErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleInvalidMappingErrors() {
        throw new HandlerNotFoundException(NO_VALID_MAPPING_FOUND);
    }

}
