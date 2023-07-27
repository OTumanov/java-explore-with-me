package ru.practicum.stsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.ewm.client.dto.HitResponseDto;
import ru.practicum.ewm.client.dto.UtilDto;
import ru.practicum.stsvc.model.Hit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HitRepository extends JpaRepository<Hit, Long>, JpaSpecificationExecutor<Hit> {

    @Query("select  count (hitId) from hits where eventId = ?1")
    long getCountHitsByEventId(Long id);

    @Query("select  new ru.practicum.ewm.client.dto.UtilDto(h.eventId, count (h)) from hits as h where h.eventId in ?1 group by h.eventId")
    List<UtilDto> getCountHitsByEventIds(List<Long> eventIds);

    //    @Query("SELECT new ru.practicum.ewm.client.dto.HitResponseDto(h.app, h.uri, COUNT(h.ip)) " +
    @Query("SELECT new ru.practicum.ewm.client.dto.HitResponseDto(h.uri, COUNT(h.ip)) " +
            "FROM hits AS h " +
            "WHERE (h.timeStamp >= :start) " +
            "AND (h.timeStamp <= :end) " +
            "AND ((:uris) IS NULL OR h.uri IN (:uris))" +
//            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitResponseDto> findViewStats(
            @Param("uris") List<String> uris, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end
    );

    //    @Query("SELECT new ru.practicum.ewm.client.dto.HitResponseDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
    @Query("SELECT new ru.practicum.ewm.client.dto.HitResponseDto(h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM hits AS h " +
            "WHERE (h.timeStamp >= :start) " +
            "AND (h.timeStamp <= :end) " +
            "AND ((:uris) IS NULL OR h.uri IN (:uris))" +
//            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitResponseDto> findViewStatsUniqueIp(
            @Param("uris") List<String> uris, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end
    );

    Optional<Hit> findOneByUriAndIp(String uri, String ip);
}