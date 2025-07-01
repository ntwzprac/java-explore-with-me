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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
        }
        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.isPinned())
                .events(events)
                .build();
        compilation = compilationRepository.save(compilation);
        Set<EventShortDto> eventDtos = events.stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toSet());
        return CompilationMapper.toDto(compilation, eventDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found: " + compId));
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found: " + compId));
        if (dto.getTitle() != null) compilation.setTitle(dto.getTitle());
        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            compilation.setEvents(events);
        }
        Compilation saved = compilationRepository.save(compilation);
        Set<EventShortDto> eventDtos = saved.getEvents().stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toSet());
        return CompilationMapper.toDto(saved, eventDtos);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        List<Compilation> comps;
        if (pinned != null) {
            comps = compilationRepository.findByPinned(pinned);
        } else {
            comps = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        return comps.stream().map(comp -> {
            Set<EventShortDto> eventDtos = comp.getEvents() == null ? new HashSet<>() :
                    comp.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toSet());
            return CompilationMapper.toDto(comp, eventDtos);
        }).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found: " + compId));
        Set<EventShortDto> eventDtos = comp.getEvents() == null ? new HashSet<>() :
                comp.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toSet());
        return CompilationMapper.toDto(comp, eventDtos);
    }
}