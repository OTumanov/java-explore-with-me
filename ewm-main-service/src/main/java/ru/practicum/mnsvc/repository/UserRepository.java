package ru.practicum.mnsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mnsvc.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}