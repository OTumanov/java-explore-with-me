package ru.practucum.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practucum.ems.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}