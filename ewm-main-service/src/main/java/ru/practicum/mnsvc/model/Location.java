package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "locations")
public class Location {

    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lon")
    private double lon;
}