package ru.practicum.mnsvc.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotEmpty
    private String email;
    private Long id;

    @NotEmpty
    private String name;
}