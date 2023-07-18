package ru.practicum.mnsvc.handler;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private Error[] errors;
    private String message;
    private String reason;
    private Status status;
    private String timestamp;
}