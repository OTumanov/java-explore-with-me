package ru.practicum.mnsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mnsvc.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}