package ru.practicum.mainservice.dto.mapper;

import lombok.Setter;
import ru.practicum.mainservice.dto.request.NewCompilationDto;
import ru.practicum.mainservice.dto.request.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.repository.EventRepository;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    @Setter
    private static EventRepository eventRepository;

    public static CompilationDto toDto(Compilation compilation) {
        if (compilation == null) return null;
        Set<EventShortDto> events = compilation.getEvents() == null ? null :
                compilation.getEvents().stream()
                        .map(eventId -> eventRepository.findById(eventId).map(EventMapper::toShortDto).orElse(null))
                        .filter(e -> e != null)
                        .collect(Collectors.toSet());
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events)
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