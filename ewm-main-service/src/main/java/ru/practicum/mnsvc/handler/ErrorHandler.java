package ru.practicum.mnsvc.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mnsvc.exceptions.ForbiddenException;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.mapper.DateTimeMapper;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(IllegalArgumentException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(Status.FORBIDDEN)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(NotFoundException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("The required object was not found.")
                .status(Status.NOT_FOUND)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handle(ForbiddenException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(Status.FORBIDDEN)
                .timestamp(DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(Throwable ex) {
        ex.printStackTrace();
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Error occurred")
                .status(Status.INTERNAL_SERVER_ERROR)
                .timestamp(DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }
}