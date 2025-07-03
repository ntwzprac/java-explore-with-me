package ru.practicum.mainservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Getting compilations with parameters: pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        log.info("Found {} compilations", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Getting compilation with id: {}", compId);
        CompilationDto compilation = compilationService.getCompilation(compId);
        log.info("Compilation found: {}", compilation.getTitle());
        return compilation;
    }
}