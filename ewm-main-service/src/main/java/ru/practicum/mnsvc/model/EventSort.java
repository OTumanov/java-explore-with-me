package ru.practicum.mnsvc.model;


import lombok.Getter;

@Getter
public enum EventSort {
    EVENT_DATE("eventDate"),
    VIEWS("views");

    private final String value;

    EventSort(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}