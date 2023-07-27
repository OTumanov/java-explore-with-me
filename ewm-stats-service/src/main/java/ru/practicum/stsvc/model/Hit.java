package ru.practicum.stsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hits")
public class Hit {

    public static final String IP_COLUMN_NAME = "ip";
    public static final String URI_COLUMN_NAME = "uri";
    public static final String ID_COLUMN_NAME = "hit_id";
    public static final String APP_COLUMN_NAME = "app_id";
    public static final String EVENT_ID_COLUMN_NAME = "event_id";
    public static final String TIMESTAMP_COLUMN_NAME = "time_stamp";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hitId;
    //    @ManyToOne(optional = false)
//    @JoinColumn(name = APP_COLUMN_NAME, nullable = false)
//    private App app;
    @Column(name = URI_COLUMN_NAME)
    private String uri;
    @Column(name = IP_COLUMN_NAME, nullable = false)
    private String ip;
    @Column(name = TIMESTAMP_COLUMN_NAME)
    private LocalDateTime timeStamp;
    @Column(name = EVENT_ID_COLUMN_NAME)
    private Long eventId;
}