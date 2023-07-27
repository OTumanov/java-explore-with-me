package ru.practicum.mnsvc.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotEmpty
    @Email
    @Length(min = 6, max = 254, message = "От 6 до 254 символов на адрес почты")
    private String email;

    @NotEmpty
    @NotBlank
    @Length(min = 2, max = 250, message = "От 2 до 250 символов для имени")
    private String name;
}