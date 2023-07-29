package ru.practucum.ems.model;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@IdClass(CompEventKey.class)
@Entity(name = "compilation_events")
public class CompEvent {
    @Id
    @Column(name = "compilation_id")
    private Long compilationId;

    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CompEvent compEvent = (CompEvent) o;
        return getCompilationId() != null && Objects.equals(getCompilationId(), compEvent.getCompilationId())
                && getEventId() != null && Objects.equals(getEventId(), compEvent.getEventId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(compilationId, eventId);
    }
}