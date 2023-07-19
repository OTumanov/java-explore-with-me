package ru.practicum.mnsvc.utils;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.mnsvc.dto.events.EventPatchDto;
import ru.practicum.mnsvc.exceptions.ForbiddenException;
import ru.practicum.mnsvc.mapper.DateTimeMapper;
import ru.practicum.mnsvc.model.*;
import ru.practicum.mnsvc.repository.CategoryRepository;
import ru.practicum.mnsvc.repository.EventRepository;
import ru.practicum.mnsvc.repository.ParticipationRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.mnsvc.utils.Util.checkIfCategoryExists;

public class EventServiceUtil {

    private EventServiceUtil() {
    }

    public static final long HOURS_LEFT_BEFORE_EVENT = 2;
    public static final long HOURS_LEFT_AFTER_PUBLICATION = 1;

    public static Specification<Event> getSpecification(EventSearchParams params, boolean publicRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getUserIds() != null) {
                for (Long userId : params.getUserIds()) {
                    predicates.add(criteriaBuilder.equal(root.get("initiator_id"), userId));
                }
            }
            if (publicRequest) {
                predicates.add(criteriaBuilder.equal(root.get("state"), PublicationState.PUBLISHED));
            } else {
                for (PublicationState state : params.getStates()) {
                    predicates.add(criteriaBuilder.equal(root.get("state"), state));
                }
            }
            if (null != params.getText()) {
                predicates.add(criteriaBuilder.like(root.get("annotation"), "%" + params.getText() + "%"));
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + params.getText() + "%"));
            }
            if (null != params.getCategories() && !params.getCategories().isEmpty()) {
                for (Long catId : params.getCategories()) {
                    predicates.add(criteriaBuilder.equal(root.get("category_id"), catId));
                }
            }
            if (null != params.getPaid()) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), params.getPaid()));
            }
            if (null != params.getRangeStart()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("published_on"), params.getRangeStart()));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("published_on"), LocalDateTime.now()));
            }
            if (null != params.getRangeEnd()) {
                predicates.add(criteriaBuilder.lessThan(root.get("published_on"), params.getRangeEnd()));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("published_on"), LocalDateTime.now()));
            }
            if (null != params.getOnlyAvailable() && params.getOnlyAvailable()) {
                predicates.add(criteriaBuilder.lessThan(root.get("participant_limit"), root.get("confirmed_Requests")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static void updateEvent(Event event, EventPatchDto update, CategoryRepository categoryRepo) {
        if (update.getAnnotation() != null) {
            event.setAnnotation(update.getAnnotation());
        }
        if (update.getCategory() != null) {
            Category category = checkIfCategoryExists(update.getCategory(), categoryRepo);
            event.setCategory(category);
        }
        if (update.getDescription() != null) {
            event.setDescription(update.getDescription());
        }
        if (update.getEventDate() != null) {
            if (!isEventDateOk(update.getEventDate())) {
                throw new ForbiddenException("the event can be changed no later than 2 hours before the start");
            }
            event.setEventDate(DateTimeMapper.toDateTime(update.getEventDate()));
        }
        if (update.getPaid() != null) {
            event.setPaid(update.getPaid());
        }
        if (update.getParticipantLimit() != null) {
            event.setParticipantLimit(update.getParticipantLimit());
        }
        if (update.getTitle() != null) {
            event.setTitle(update.getTitle());
        }
    }

    public static boolean isEventDateOk(String eventDate) {
        LocalDateTime date = DateTimeMapper.toDateTime(eventDate);
        return date.isAfter(LocalDateTime.now().plusHours(2));
    }

    public static Event addView(Event event, EventRepository eventRepo) {
        long views = event.getViews() + 1;
        event.setViews(views);
        return eventRepo.save(event);
    }

    public static void addViewForEach(List<Event> events, EventRepository eventRepo) {
        for (Event event : events) {
            addView(event, eventRepo);
        }
    }

    public static void increaseConfirmedRequest(Event event, EventRepository eventRepo) {
        int confirmedRec = event.getConfirmedRequests() + 1;
        event.setConfirmedRequests(confirmedRec);
        eventRepo.save(event);
    }

    public static void checkParticipationLimit(Event event, ParticipationRepository participationRepo) {
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            List<Participation> participations = participationRepo
                    .findAllByEventIdAndState(event.getId(), ParticipationState.PENDING);

            for (Participation par : participations) {
                par.setState(ParticipationState.REJECT);
                participationRepo.save(par);
            }
        }
    }

    public static boolean isMayPublish(Event event) {
        return event.getEventDate().isAfter(LocalDateTime.now().plusHours(HOURS_LEFT_AFTER_PUBLICATION));
    }
}