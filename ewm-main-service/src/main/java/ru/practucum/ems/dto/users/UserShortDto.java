package ru.practucum.ems.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}