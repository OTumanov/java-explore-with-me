package ru.practicum.mnsvc.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.mnsvc.mapper.DateTimeMapper;
import ru.practicum.mnsvc.utils.Util;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventSearchParams {
    private List<Long> userIds;
    private String text;
    private List<PublicationState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean paid;
    private Boolean onlyAvailable;
    private Integer from;
    private Integer size;
    private EventSort sort;

    //EventAdminController
    public EventSearchParams(List<Long> usersIds,
                             List<String> states,
                             List<Long> categories,
                             String rangeStart,
                             String rangeEnd,
                             Integer from,
                             Integer size) {
        this.userIds = usersIds;
        if (states != null) {
            this.states = Util.mapToStates(states);
        }
        this.categories = categories;
        if (rangeStart != null) {
            this.rangeStart = DateTimeMapper.toDateTime(rangeStart);
        }
        if (rangeEnd != null) {
            this.rangeEnd = DateTimeMapper.toDateTime(rangeEnd);
        }
        this.from = from;
        this.size = size;
    }


    //EventPublicController
    public EventSearchParams(String text,
                             List<Long> categories,
                             Boolean paid,
                             String rangeStart,
                             String rangeEnd,
                             Boolean onlyAvailable,
                             String sort,
                             Integer from,
                             Integer size) {
        if (text != null) {
            this.text = text.toLowerCase();
        }
        this.categories = categories;
        this.paid = paid;
        if (rangeStart != null) {
            this.rangeStart = DateTimeMapper.toDateTime(rangeStart);
        }
        if (rangeEnd != null) {
            this.rangeEnd = DateTimeMapper.toDateTime(rangeEnd);
        }
        this.onlyAvailable = onlyAvailable;
        if (sort != null) {
            this.sort = Util.parseSort(sort);
        }
        this.from = from;
        this.size = size;
    }

    @Override
    public String toString() {
        return "EventSearchParams{" +
                "userIds=" + userIds +
                ", text='" + text + '\'' +
                ", states=" + states +
                ", categories=" + categories +
                ", rangeStart=" + rangeStart +
                ", rangeEnd=" + rangeEnd +
                ", paid=" + paid +
                ", onlyAvailable=" + onlyAvailable +
                ", from=" + from +
                ", size=" + size +
                ", sort=" + sort +
                '}';
    }
}