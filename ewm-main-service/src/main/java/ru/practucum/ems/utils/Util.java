package ru.practucum.ems.utils;


import ru.practicum.sd.dto.UtilDto;
import ru.practucum.ems.dto.comments.CommentPatchDto;
import ru.practucum.ems.dto.comments.CommentPostDto;
import ru.practucum.ems.model.Event;
import ru.practucum.ems.model.EventSort;
import ru.practucum.ems.model.PublicationState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
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

    public static void checkTextInComment(CommentPostDto dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalArgumentException("Не заполнено обязательное поле в дто!");
        }
    }

    public static void checkTextInComment(CommentPatchDto dto) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalArgumentException("Не заполнено обязательное поле в дто!");
        }
    }
}