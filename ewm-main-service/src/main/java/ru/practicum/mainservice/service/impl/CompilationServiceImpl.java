package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.CompilationMapper;
import ru.practicum.mainservice.dto.mapper.EventMapper;
import ru.practicum.mainservice.dto.request.NewCompilationDto;
import ru.practicum.mainservice.dto.request.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.dto.response.EventShortDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.CompilationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        final Set<Event> events = getEventsByIds(dto.getEvents());
        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.isPinned())
                .events(events)
                .build();
        compilation = compilationRepository.save(compilation);
        return toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = getCompilationOrThrow(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = getCompilationOrThrow(compId);
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(getEventsByIds(dto.getEvents()));
        }
        Compilation saved = compilationRepository.save(compilation);
        return toCompilationDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        final List<Compilation> comps;
        if (pinned != null) {
            comps = compilationRepository.findByPinned(pinned);
        } else {
            comps = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        return comps.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        Compilation comp = getCompilationOrThrow(compId);
        return toCompilationDto(comp);
    }

    private Compilation getCompilationOrThrow(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found: " + compId));
    }

    private Set<Event> getEventsByIds(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(eventRepository.findAllById(eventIds));
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        Set<EventShortDto> eventDtos = Optional.ofNullable(compilation.getEvents())
                .orElse(Collections.emptySet())
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toSet());
        return CompilationMapper.toDto(compilation, eventDtos);
    }
}