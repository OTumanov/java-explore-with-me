package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "locations")
public class Location {

    public static final String ID_COLUMN_NAME = "location_id";
    public static final String LAT_COLUMN_NAME = "lat";
    public static final String LON_COLUMN_NAME = "lon";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = LAT_COLUMN_NAME)
    private double lat;
    @Column(name = LON_COLUMN_NAME)
    private double lon;
}