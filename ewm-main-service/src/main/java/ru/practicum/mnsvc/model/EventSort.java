package ru.practicum.mnsvc.model;

public enum EventSort {
    EVENT_DATE("event_date"),
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