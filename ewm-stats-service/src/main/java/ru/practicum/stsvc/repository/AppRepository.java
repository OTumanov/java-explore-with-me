package ru.practicum.stsvc.repository;

import ru.practicum.stsvc.model.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppRepository extends JpaRepository<App, Long> {
    @Query("select ap from App as ap where ap.name like ?1")
    Optional<App> findByName(String appName);
}