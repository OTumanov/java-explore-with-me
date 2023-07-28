package ru.practicum.mnsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mnsvc.model.CompEvent;

public interface CompEventsRepository extends JpaRepository<CompEvent, Long> {
}