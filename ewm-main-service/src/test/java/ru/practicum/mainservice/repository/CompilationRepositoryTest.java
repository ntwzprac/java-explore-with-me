package ru.practicum.mainservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.mainservice.model.Compilation;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CompilationRepositoryTest {
    @Autowired
    private CompilationRepository compilationRepository;

    @Test
    void saveAndFindById() {
        Compilation compilation = new Compilation();
        compilation.setTitle("compilation1");
        Compilation saved = compilationRepository.save(compilation);
        Optional<Compilation> found = compilationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("compilation1");
    }
}