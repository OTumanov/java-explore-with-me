package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "events")
public class Event {

    public static final String PAID_COLUMN_NAME = "paid";
    public static final String STATE_COLUMN_NAME = "state";
    public static final String TITLE_COLUMN_NAME = "title";
    public static final String VIEWS_COLUMN_NAME = "views";
    public static final String ID_COLUMN_NAME = "event_id";
    public static final String CATEGORY_COLUMN_NAME = "category_id";
    public static final String LOCATION_COLUMN_NAME = "location_id";
    public static final String ANNOTATION_COLUMN_NAME = "annotation";
    public static final String CREATED_ON_COLUMN_NAME = "created_on";
    public static final String EVENT_DATE_COLUMN_NAME = "event_date";
    public static final String INITIATOR_COLUMN_NAME = "initiator_id";
    public static final String PUBLISHED_ON_COLUMN_NAME = "published_on";
    public static final String DESCRIPTION_ON_COLUMN_NAME = "description";
    public static final String PARTICIPANT_LIMIT_COLUMN_NAME = "participant_limit";
    public static final String REQUEST_MODERATION_COLUMN_NAME = "request_moderation";
    public static final String CONFIRMED_REQUESTS_COLUMN_NAME = "confirmed_requests";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = ANNOTATION_COLUMN_NAME, nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = CATEGORY_COLUMN_NAME)
    private Category category;
    @Column(name = CONFIRMED_REQUESTS_COLUMN_NAME, nullable = false)
    private Integer confirmedRequests;
    @Column(name = CREATED_ON_COLUMN_NAME, nullable = false)
    private LocalDateTime createdOn;
    @Column(name = DESCRIPTION_ON_COLUMN_NAME, nullable = false)
    private String description;
    @Column(name = EVENT_DATE_COLUMN_NAME, nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = INITIATOR_COLUMN_NAME)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = LOCATION_COLUMN_NAME)
    private Location location;
    @Column(name = PAID_COLUMN_NAME)
    private Boolean paid;
    @Column(name = PARTICIPANT_LIMIT_COLUMN_NAME, nullable = false)
    private Integer participantLimit;
    @Column(name = PUBLISHED_ON_COLUMN_NAME, nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = REQUEST_MODERATION_COLUMN_NAME, nullable = false)
    private Boolean requestModeration;
    @Column(name = STATE_COLUMN_NAME, nullable = false)
    private PublicationState state;
    @Column(name = TITLE_COLUMN_NAME, nullable = false)
    private String title;
    @Column(name = VIEWS_COLUMN_NAME, nullable = false)
    private Long views;
}