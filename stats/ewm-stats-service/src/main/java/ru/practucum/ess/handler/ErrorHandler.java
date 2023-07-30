package ru.practucum.ess.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practucum.ess.exception.ForbiddenException;
import ru.practucum.ess.mapper.DateTimeMapper;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.practucum.ess.handler.ApiError handle(ForbiddenException exception) {
        log.info("Wrond validation", exception);
        exception.printStackTrace();
        return ru.practucum.ess.handler.ApiError.builder()
                .message(exception.getMessage())
                .reason("Wrond validation")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(ru.practucum.ess.mapper.DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.practucum.ess.handler.ApiError handle(MissingServletRequestParameterException exception) {
        log.info("Wrond validation", exception);
        exception.printStackTrace();
        return ru.practucum.ess.handler.ApiError.builder()
                .message(exception.getMessage())
                .reason("Wrond validation")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(ru.practucum.ess.mapper.DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ru.practucum.ess.handler.ApiError handle(Throwable throwable) {
        log.info("Unexpected internal server error", throwable);
        throwable.printStackTrace();
        return ApiError.builder()
                .message(throwable.getMessage())
                .reason("Error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }
}