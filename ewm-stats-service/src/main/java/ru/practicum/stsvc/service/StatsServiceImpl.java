package ru.practicum.stsvc.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.dto.UtilDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.stsvc.mapper.HitMapper;
import ru.practicum.stsvc.model.Hit;
import ru.practicum.stsvc.repository.AppRepository;
import ru.practicum.stsvc.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public Hit postHit(HitPostDto dto) {
        if(hitRepository.findOneByUriAndIp(dto.getUri(), dto.getIp()).isPresent()) {
            log.info("Такой хит уже есть в базе -- {}.", hitRepository.findOneByUriAndIp(dto.getUri(), dto.getIp()).get());
            return HitMapper.toModel(dto);
        }
//        App app = appRepo.findByName(dto.getApp()).orElse(null);
//        if (app == null) {
//            app = appRepo.save(new App(dto.getApp()));
//        }
//        Hit hit = HitMapper.toModel(dto, null);
        Hit hit = HitMapper.toModel(dto);
        hitRepository.save(hit);

        return hit;
    }

    @Override
    public List<HitResponseDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return hitRepository.findViewStatsUniqueIp(uris, start, end);
        } else {
            return hitRepository.findViewStats(uris, start, end);
        }
    }

    @Override
    public Long getViewsByEventId(Long eventId) {
        log.info("Найдено просмотров данного события {}",hitRepository.getCountHitsByEventId(eventId));
        return hitRepository.getCountHitsByEventId(eventId);
    }

    @Override
    public BoxDto getViewsByEventIds(List<String> ids) {
        List<Long> eventIds = mapUtilDtoList(ids);
        List<UtilDto> utilDtos = hitRepository.getCountHitsByEventIds(eventIds);
        return new BoxDto(utilDtos);
    }

    private List<Long> mapUtilDtoList(List<String> ids) {
        return ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}