package ru.practucum.ems.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.users.NewUserDto;
import ru.practucum.ems.dto.users.UserDto;
import ru.practucum.ems.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")

public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос информации о пользователях - ##{}", ids);
        return userService.findUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Validated @RequestBody NewUserDto dto) {
        log.info("Добавление нового пользователя - \"{}\", \"{}\"", dto.getName(), dto.getEmail());
        return userService.postUser(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Удаление пользователя #{}", userId);
        userService.deleteUser(userId);
    }
}