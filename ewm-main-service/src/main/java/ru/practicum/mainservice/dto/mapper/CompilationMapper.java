package ru.practicum.mainservice.dto.mapper;

import ru.practicum.mainservice.dto.request.NewCompilationDto;
import ru.practicum.mainservice.dto.request.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.model.Compilation;

public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation) {
        if (compilation == null) return null;
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(compilation.getEvents())
                .build();
    }

    public static Compilation toEntity(NewCompilationDto dto) {
        if (dto == null) return null;
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.isPinned())
                .events(dto.getEvents())
                .build();
    }

    public static void updateEntity(Compilation compilation, UpdateCompilationRequest dto) {
        if (dto.getTitle() != null) compilation.setTitle(dto.getTitle());
        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getEvents() != null) compilation.setEvents(dto.getEvents());
    }
} 