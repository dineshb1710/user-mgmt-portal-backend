package com.dineshb.projects.usermgmt.portal.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class HttpResponse {

    private int statusCode;
    private String reason;
    private String message;
    private HttpStatus httpStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date timeStamp;
}
