package ru.practucum.ess.handler;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
public class ApiError {
    private List<Error> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private String timestamp;
}