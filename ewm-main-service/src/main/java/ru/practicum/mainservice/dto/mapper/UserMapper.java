package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.dto.request.NewUserRequest;
import ru.practicum.mainservice.dto.response.UserDto;
import ru.practicum.mainservice.dto.response.UserShortDto;

public class UserMapper {
    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toEntity(NewUserRequest dto) {
        if (dto == null) return null;
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User user) {
        if (user == null) return null;
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
} 