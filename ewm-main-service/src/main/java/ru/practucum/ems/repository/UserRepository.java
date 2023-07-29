package ru.practucum.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practucum.ems.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}