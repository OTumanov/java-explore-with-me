package ru.practicum.stsvc.service;

import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.dto.UtilDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.stsvc.mapper.HitMapper;
import ru.practicum.stsvc.model.App;
import ru.practicum.stsvc.model.Hit;
import ru.practicum.stsvc.repository.AppRepository;
import ru.practicum.stsvc.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepo;
    private final AppRepository appRepo;

    @Override
    @Transactional
    public Hit postHit(HitPostDto dto) {
        App app = appRepo.findByName(dto.getApp()).orElse(null);
        if (app == null) {
            app = appRepo.save(new App(dto.getApp()));
        }
        Hit hit = HitMapper.toModel(dto, app);
        hitRepo.save(hit);

        return hit;
    }

    @Override
    public List<HitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return hitRepo.findViewStatsUniqueIp(uris, start, end);
        } else {
            return hitRepo.findViewStats(uris, start, end);
        }
    }

    @Override
    public Long getViewsByEventId(Long eventId) {
        return hitRepo.getCountHitsByEventId(eventId);
    }

    @Override
    public BoxDto getViewsByEventIds(List<String> ids) {
        List<Long> eventIds = mapUtilDtoList(ids);
        List<UtilDto> utilDtos = hitRepo.getCountHitsByEventIds(eventIds);
        return new BoxDto(utilDtos);
    }

    private List<Long> mapUtilDtoList(List<String> ids) {
        return ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}