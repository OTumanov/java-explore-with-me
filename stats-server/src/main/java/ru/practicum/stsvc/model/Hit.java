package ru.practicum.stsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hits")
public class Hit {

    @Id
    @Column(name = "hit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hitId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "app_id", nullable = false)
    private App app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;
    @Column(name = "event_id")
    private Long eventId;
}