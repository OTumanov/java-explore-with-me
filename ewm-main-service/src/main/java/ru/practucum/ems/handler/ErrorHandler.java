package ru.practucum.ems.handler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practucum.ems.exceptions.ForbiddenException;
import ru.practucum.ems.exceptions.NotFoundException;
import ru.practucum.ems.mapper.DateTimeMapper;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor
@RestControllerAdvice
public class ErrorHandler {

    //    400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(IllegalArgumentException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Не соблюдены условия для запроса")
                .status(Status.BAD_REQUEST)
                .build();
    }

    //    400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(MethodArgumentNotValidException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Не прошла валидация в классе")
                .status(Status.BAD_REQUEST)
                .build();
    }

    //    400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(MissingServletRequestParameterException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Не прошла валидация в классе")
                .status(Status.BAD_REQUEST)
                .build();
    }

    //    403
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handle(ForbiddenException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("В запросе отказано")
                .status(Status.FORBIDDEN)
                .timestamp(DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }

    //    404
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(NotFoundException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Требуемый объект не найден")
                .status(Status.NOT_FOUND)
                .build();
    }

    //    409
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(DataIntegrityViolationException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Конфликт событий")
                .status(Status.CONFLICT)
                .build();
    }

    //    500
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(Throwable ex) {
        ex.printStackTrace();
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Внутренняя ошибка сервера")
                .status(Status.INTERNAL_SERVER_ERROR)
                .timestamp(DateTimeMapper.toString(LocalDateTime.now()))
                .build();
    }
}