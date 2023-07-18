package ru.practicum.stsvc.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "apps")
public class App {

    @Id
    @Column(name = "app_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name")
    private String name;

    public App(String name) {
        this.name = name;
    }
}