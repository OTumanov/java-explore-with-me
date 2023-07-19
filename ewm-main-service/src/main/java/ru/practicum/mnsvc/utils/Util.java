package ru.practicum.mnsvc.utils;


import ru.practicum.ewm_ms.client.dto.UtilDto;
import ru.practicum.mnsvc.exceptions.NotFoundException;
import ru.practicum.mnsvc.model.*;
import ru.practicum.mnsvc.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    private Util() {
    }

    public static String getUserNotFoundMessage(long userId) {
        return "User with id=" + userId + " was not found.";
    }

    public static String getEventNotFoundMessage(long eventId) {
        return "Event with id=" + eventId + " was not found.";
    }

    public static String getCategoryNotFoundMessage(long categoryId) {
        return "Category with id=" + categoryId + " was not found";
    }

    public static String getCompilationNotFoundMessage(long compilationId) {
        return "Compilation with id:=" + compilationId + " was not found";
    }

    public static String getParticipationNotFoundMessage(long requestId) {
        return "Participation request with id=" + requestId + " was not found.";
    }

    public static User checkIfUserExists(Long userId, UserRepository repo) {
        User user = repo.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(getUserNotFoundMessage(userId));
        }
        return user;
    }

    public static Event checkIfEventExists(Long eventId, EventRepository repo) {
        Event event = repo.findById(eventId).orElse(null);
        if (event == null) {
            throw new NotFoundException(getEventNotFoundMessage(eventId));
        }
        return event;
    }

    public static Participation checkIfParticipationRequestExists(Long requestId, ParticipationRepository repo) {
        Participation par = repo.findById(requestId).orElse(null);
        if (par == null) {
            throw new NotFoundException(getParticipationNotFoundMessage(requestId));
        }
        return par;
    }

    public static Compilation checkIfCompilationExists(Long compId, CompilationRepository repo) {
        Compilation compilation = repo.findById(compId).orElse(null);
        if (compilation == null) {
            throw new NotFoundException(getCompilationNotFoundMessage(compId));
        }
        return compilation;
    }

    public static Category checkIfCategoryExists(Long catId, CategoryRepository repo) {
        Category category = repo.findById(catId).orElse(null);
        if (category == null) {
            throw new NotFoundException(getCategoryNotFoundMessage(catId));
        }
        return category;
    }

    public static EventSort parseSort(String str) {
        EventSort sort;
        try {
            sort = EventSort.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid Sort value: " + str);
        }
        return sort;
    }

    public static List<PublicationState> mapToStates(List<String> stateNames) {
        List<PublicationState> result = new ArrayList<>();
        for (String name : stateNames) {
            PublicationState state = PublicationState.valueOf(name.toUpperCase());
            result.add(state);
        }
        return result;
    }

    public static Category mapIdToCategory(Long catId, CategoryRepository repo) {
        return repo.findById(catId).orElseThrow(() -> new NotFoundException(getCategoryNotFoundMessage(catId)));
    }

    public static List<Long> getEventIdsList(List<Event> events) {
        return events.stream().map(Event::getId).collect(Collectors.toList());
    }

    public static Long matchLongValueByEventId(List<UtilDto> utilDtos, Long eventId) {
        UtilDto dto = utilDtos.stream()
                .filter(utilDto -> utilDto.getEntityId().equals(eventId))
                .findFirst()
                .orElse(new UtilDto(eventId, 0L));
        return dto.getCount();
    }

    public static Integer matchIntValueByEventId(List<UtilDto> utilDtos, Long eventId) {
        UtilDto dto = utilDtos.stream()
                .filter(utilDto -> utilDto.getEntityId().equals(eventId))
                .findFirst()
                .orElse(new UtilDto(eventId, 0L));
        return dto.getCount().intValue();
    }
}