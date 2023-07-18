package ru.practicum.mnsvc.exceptions;

public class EnumParseException extends RuntimeException {
    public EnumParseException(String message) {
        super(message);
    }
}