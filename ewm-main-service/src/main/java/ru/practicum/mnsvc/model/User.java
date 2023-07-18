package ru.practicum.mnsvc.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    public static final int MAX_EMAIL_LENGTH = 512;
    public static final String NAME_COLUMN_NAME = "username";
    public static final String ID_COLUMN_NAME = "user_id";
    public static final String EMAIL_COLUMN_NAME = "email";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = EMAIL_COLUMN_NAME, nullable = false, length = MAX_EMAIL_LENGTH)
    private String email;
    @Column(name = NAME_COLUMN_NAME, nullable = false)
    private String name;
}