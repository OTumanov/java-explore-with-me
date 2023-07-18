package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "categories")
public class Category {

    public static final String ID_COLUMN_NAME = "category_id";
    public static final String NAME_COLUMN_NAME = "category_name";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = NAME_COLUMN_NAME)
    private String name;
}