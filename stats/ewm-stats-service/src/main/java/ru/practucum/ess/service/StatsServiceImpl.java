package ru.practucum.ess.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.sd.dto.EndpointHitDto;
import ru.practicum.sd.dto.ViewStatsDto;
import ru.practucum.ess.exception.ForbiddenException;
import ru.practucum.ess.mapper.EndpointHitMapper;
import ru.practucum.ess.mapper.ViewStatMapper;
import ru.practucum.ess.model.EndpointHit;
import ru.practucum.ess.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHitModel = EndpointHitMapper.toModel(endpointHitDto);
        return EndpointHitMapper.toDto(statsRepository.save(endpointHitModel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start) || start.isAfter(end)) {
            throw new ForbiddenException("дата начала поиска не может быть позже конца");
        }
        if (unique) {
            return ViewStatMapper.toDto(statsRepository.findViewStatsUniqueIp(uris, start, end));
        } else {
            return ViewStatMapper.toDto(statsRepository.findViewStats(uris, start, end));
        }
    }

}