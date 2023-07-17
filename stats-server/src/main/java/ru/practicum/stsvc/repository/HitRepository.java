package ru.practicum.stsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stsvc.dto.UtilDto;
import ru.practicum.stsvc.model.Hit;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long>, JpaSpecificationExecutor<Hit> {

        @Query("select  count (hitId) from Hit where eventId = ?1")
        long getCountHitsByEventId(Long id);

        @Query("select new ru.practicum.stsvc.dto.UtilDto(h.hitId, count (h)) from Hit as h where h.uri in ?1 group by h.hitId")
        List<UtilDto> getContHitsByUris(List<String> uris);

        @Query("select  new ru.practicum.stsvc.dto.UtilDto(h.eventId, count (h)) from Hit as h where h.eventId in ?1 group by h.eventId")
        List<UtilDto> getCountHitsByEventIds(List<Long> eventIds);
}