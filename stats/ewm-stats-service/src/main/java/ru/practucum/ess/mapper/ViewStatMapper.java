package ru.practucum.ess.mapper;

import ru.practicum.sd.dto.ViewStatsDto;
import ru.practucum.ess.model.ViewStat;

import java.util.List;
import java.util.stream.Collectors;

public class ViewStatMapper {
    public static List<ViewStatsDto> toDto(List<ViewStat> viewStats) {
        return viewStats
                .stream()
                .map(viewStat -> new ViewStatsDto(viewStat.getApp(), viewStat.getUri(), viewStat.getHits()))
                .collect(Collectors.toList());
    }
}