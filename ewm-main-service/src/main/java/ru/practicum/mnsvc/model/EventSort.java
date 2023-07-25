package ru.practicum.mnsvc.model;

public enum EventSort {
    EVENT_DATE("eventDate"),
    VIEWS("views");

    private final String value;

    EventSort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}