package ru.practicum.mnsvc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mnsvc.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findAllByCategoryId(Long categoryId);

    @Query("select e from Event as e where e.id in ?1")
    List<Event> findAll(List<Long> ids);


    //    @Query("select e from events e where upper(e.description) like upper(concat('%', ?1, '%')) or upper(e.annotation) like upper(concat('%', ?1, '%'))")
//    @Query("select e from Event e " +
//            "where upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.annotation) like upper(concat('%', ?1, '%'))" +
//            "order by ?3")
//    @Query("select e from Event e " +
//            "where upper(e.description) like upper(concat('%', ?1, '%')) " +
//            "or upper(e.annotation) like upper(concat('%', ?1, '%')) ")
//    Page<Event> findAllByText(String text, Pageable pageable);
}