package ru.practucum.ems.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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