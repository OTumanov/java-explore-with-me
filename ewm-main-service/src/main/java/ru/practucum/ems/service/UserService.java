package ru.practucum.ems.service;


import ru.practucum.ems.dto.users.NewUserDto;
import ru.practucum.ems.dto.users.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto postUser(NewUserDto dto);

    void deleteUser(Long userId);
}