package ru.practicum.mnsvc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.mnsvc.mapper.DateTimeMapper;
import ru.practicum.mnsvc.model.Event;
import ru.practicum.mnsvc.model.EventSearchParams;
import ru.practicum.mnsvc.model.EventSort;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventServiceUtil {

    public static final long HOURS_LEFT_BEFORE_EVENT = 2;
    public static final long HOURS_LEFT_AFTER_PUBLICATION = 1;

    public static Sort getSort(EventSort sortBy) {
        if (sortBy.equals(EventSort.EVENT_DATE)) {
            return Sort.by(sortBy.getValue());
        }
        if (sortBy.equals(EventSort.VIEWS)) {
            return Sort.by(sortBy.getValue());
        }
        return Sort.unsorted();
    }

    public static Specification<Event> getSpecification(EventSearchParams params, boolean publicRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getUserIds() != null) {
                for (Long userId : params.getUserIds()) {
                    System.out.println(userId);
                    predicates.add(criteriaBuilder.in(root.get("initiator").get("id")).value(userId));
                }
            }
//            if (publicRequest) {
//                predicates.add(criteriaBuilder.equal(root.get("state"), PublicationState.PUBLISHED));
//            } else if (null != params.getStates()) {
//                for (PublicationState state : params.getStates()) {
//                    predicates.add(criteriaBuilder.in(root.get("state")).value(state));
//                }
//            }
            if (null != params.getText()) {
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("annotation"), "%" + params.getText() + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + params.getText() + "%")
                );
            }
            if (null != params.getCategories() && !params.getCategories().isEmpty()) {
                for (Long catId : params.getCategories()) {
                    predicates.add(criteriaBuilder.in(root.get("category").get("id")).value(catId));
                }
            }
            if (null != params.getPaid()) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), params.getPaid()));
            }

            if (null != params.getRangeStart()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("publishedOn"), params.getRangeStart()));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("publishedOn"), LocalDateTime.now()));
            }
            if (null != params.getRangeEnd()) {
                predicates.add(criteriaBuilder.lessThan(root.get("publishedOn"), params.getRangeEnd()));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("publishedOn"), LocalDateTime.now()));
            }
            if (null != params.getOnlyAvailable() && params.getOnlyAvailable()) {
                predicates.add(criteriaBuilder.lessThan(root.get("participantLimit"), root.get("confirmedRequests")));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static boolean isEventDateOk(String eventDate) {
        LocalDateTime date = DateTimeMapper.toDateTime(eventDate);
        return date.isAfter(LocalDateTime.now().plusHours(HOURS_LEFT_BEFORE_EVENT));
    }

    public static boolean isMayPublish(Event event) {
        return event.getEventDate().isAfter(LocalDateTime.now().plusHours(HOURS_LEFT_AFTER_PUBLICATION));
    }
}