package com.dineshb.projects.usermgmt.portal.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class HttpResponse {

    private int statusCode;
    private String reason;
    private String message;
    private HttpStatus httpStatus;
}
