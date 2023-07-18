package ru.practicum.mnsvc.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.users.NewUserDto;
import ru.practicum.mnsvc.dto.users.UserDto;
import ru.practicum.mnsvc.service.UserService;
import ru.practicum.mnsvc.utils.CommonValidMarker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";

    private final UserService userService;

    @GetMapping
    public List<UserDto> findUsers(@RequestParam List<Long> ids,
                                   @PositiveOrZero
                                   @RequestParam(defaultValue = DEFAULT_FROM) Integer from,
                                   @Positive
                                   @RequestParam(defaultValue = DEFAULT_SIZE) Integer size) {
        log.info("Searching Users ids: {}", ids);
        return userService.findUsers(ids, from, size);
    }

    @PostMapping
    public UserDto postUser(@Validated({CommonValidMarker.class})
                            @RequestBody NewUserDto dto) {
        log.info("Post User {}", dto);
        return userService.postUser(dto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive
                           @PathVariable Long userId) {
        log.info("Delete User id: {}", userId);
        userService.deleteUser(userId);
    }
}