package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.mapper.CompilationMapper;
import ru.practicum.mainservice.dto.request.NewCompilationDto;
import ru.practicum.mainservice.dto.request.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.CompilationService;
import ru.practicum.mainservice.dto.mapper.EventMapper;
import ru.practicum.mainservice.dto.response.EventShortDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationRepository.save(CompilationMapper.toEntity(dto));
        Set<EventShortDto> events = new HashSet<>();
        if (compilation.getEvents() != null) {
            events = compilation.getEvents().stream()
                    .map(eventId -> eventRepository.findById(eventId).map(EventMapper::toShortDto).orElse(null))
                    .filter(e -> e != null)
                    .collect(Collectors.toSet());
        }
        return CompilationMapper.toDto(compilation, events);
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
        CompilationMapper.updateEntity(compilation, dto);
        Compilation saved = compilationRepository.save(compilation);
        Set<EventShortDto> events = new HashSet<>();
        if (saved.getEvents() != null) {
            events = saved.getEvents().stream()
                    .map(eventId -> eventRepository.findById(eventId).map(EventMapper::toShortDto).orElse(null))
                    .filter(e -> e != null)
                    .collect(Collectors.toSet());
        }
        return CompilationMapper.toDto(saved, events);
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
            Set<EventShortDto> events = new HashSet<>();
            if (comp.getEvents() != null) {
                events = comp.getEvents().stream()
                        .map(eventId -> eventRepository.findById(eventId).map(EventMapper::toShortDto).orElse(null))
                        .filter(e -> e != null)
                        .collect(Collectors.toSet());
            }
            return CompilationMapper.toDto(comp, events);
        }).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found: " + compId));
        Set<EventShortDto> events = new HashSet<>();
        if (comp.getEvents() != null) {
            events = comp.getEvents().stream()
                    .map(eventId -> eventRepository.findById(eventId).map(EventMapper::toShortDto).orElse(null))
                    .filter(e -> e != null)
                    .collect(Collectors.toSet());
        }
        return CompilationMapper.toDto(comp, events);
    }
}