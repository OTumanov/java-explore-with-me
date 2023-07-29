package ru.practucum.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practucum.ems.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}