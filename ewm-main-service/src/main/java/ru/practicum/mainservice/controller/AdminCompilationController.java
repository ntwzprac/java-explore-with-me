package ru.practicum.mainservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.NewCompilationDto;
import ru.practicum.mainservice.dto.request.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.response.CompilationDto;
import ru.practicum.mainservice.service.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Creating new compilation: {}", dto.getTitle());
        CompilationDto result = compilationService.saveCompilation(dto);
        log.info("Compilation successfully created with id: {}", result.getId());
        return result;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Deleting compilation with id: {}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Compilation with id: {} successfully deleted", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId, @RequestBody @Valid UpdateCompilationRequest dto) {
        log.info("Updating compilation with id: {}", compId);
        CompilationDto result = compilationService.updateCompilation(compId, dto);
        log.info("Compilation with id: {} successfully updated", compId);
        return result;
    }
}