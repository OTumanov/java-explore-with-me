package ru.practicum.mnsvc.handler;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private Error[] errors;
    private String message;
    private String reason;
    private Status status;
    private String timestamp;
}