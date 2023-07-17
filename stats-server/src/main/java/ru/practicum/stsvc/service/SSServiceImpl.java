package ru.practicum.stsvc.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.stsvc.dto.BoxDto;
import ru.practicum.stsvc.dto.HitPostDto;
import ru.practicum.stsvc.dto.HitResponseDto;
import ru.practicum.stsvc.dto.UtilDto;
import ru.practicum.stsvc.mapper.HitMapper;
import ru.practicum.stsvc.model.App;
import ru.practicum.stsvc.model.Hit;
import ru.practicum.stsvc.model.HitSearchParams;
import ru.practicum.stsvc.repository.AppRepository;
import ru.practicum.stsvc.repository.HitRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SSServiceImpl implements SSService {

    private final HitRepository hitRepo;
    private final AppRepository appRepo;

    @Override
    @Transactional
    public void postHit(HitPostDto dto) {
        App app = appRepo.findByName(dto.getApp()).orElse(null);
        if (app == null) {
            app = appRepo.save(new App(dto.getApp()));
        }
        Hit hit = HitMapper.toModel(dto, app);
        hitRepo.save(hit);
    }

    @Override
    public List<HitResponseDto> getHits(HitSearchParams params) {
        Specification<Hit> specification = getSpecification(params);
        List<Hit> hits = hitRepo.findAll(specification);
        List<String> uris = hits.stream().map(Hit::getUri).collect(Collectors.toList());
        List<UtilDto> hitCounts = hitRepo.getContHitsByUris(uris);
        return mapHitsWithCountHits(hits, hitCounts);
    }

    private List<HitResponseDto> mapHitsWithCountHits(List<Hit> hits, List<UtilDto> hitCounts) {
        List<HitResponseDto> result = new ArrayList<>();
        for (Hit hit : hits) {
            UtilDto hitCount = hitCounts.stream()
                    .filter(utilDto -> utilDto.getEntityId().equals(hit.getHitId()))
                    .findFirst()
                    .orElse(null);
            assert hitCount != null;
            result.add(HitMapper.toDto(hit, hitCount.getCount()));
        }
        return result;
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

    private Specification<Hit> getSpecification(HitSearchParams params) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (null != params.getStart()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timeStamp"), params.getStart()));
            }
            if (null != params.getEnd()) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timeStamp"), params.getEnd()));
            }
            if (null != params.getUris() && !params.getUris().isEmpty()) {
                for (String uri : params.getUris()) {
                    predicates.add(criteriaBuilder.equal(root.get("uri"), uri));
                }
            }
            if (null != params.getUnique()) {
                query.distinct(true);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}