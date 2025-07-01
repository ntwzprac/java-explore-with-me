package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);
} 