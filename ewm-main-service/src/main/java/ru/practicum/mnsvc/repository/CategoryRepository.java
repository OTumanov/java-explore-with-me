package ru.practicum.mnsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mnsvc.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}