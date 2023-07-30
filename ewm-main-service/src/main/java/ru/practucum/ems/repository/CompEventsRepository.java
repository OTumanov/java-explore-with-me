package ru.practucum.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practucum.ems.model.CompEvent;

public interface CompEventsRepository extends JpaRepository<CompEvent, Long> {
}