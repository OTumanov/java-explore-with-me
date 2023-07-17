package ru.practicum.stsvc.dto;

public class UtilDto {
    private Long entityId;
    private Long count;

    public UtilDto(Long entityId, Long count) {
        this.entityId = entityId;
        this.count = count;
    }

    public UtilDto() {
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}