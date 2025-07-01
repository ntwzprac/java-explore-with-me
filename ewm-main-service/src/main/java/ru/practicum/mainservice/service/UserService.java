package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.request.NewUserRequest;
import ru.practicum.mainservice.dto.response.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(NewUserRequest dto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);
}