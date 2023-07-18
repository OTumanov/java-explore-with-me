package ru.practicum.mnsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mnsvc.model.Participation;
import ru.practicum.mnsvc.model.ParticipationState;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByRequesterId(Long requesterId);

    Optional<Participation> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Participation> findAllByEventId(Long eventId);

    List<Participation> findAllByEventIdAndState(Long event_id, ParticipationState state);
}