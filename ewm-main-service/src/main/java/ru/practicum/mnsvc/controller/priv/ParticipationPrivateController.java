package ru.practicum.mnsvc.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.service.ParticipationService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationDto> getInfoAboutAllParticipation(@Positive @PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя {} на участие в чужих событиях", userId);
        return participationService.getInfoAboutAllParticipation(userId);
    }

    @PostMapping
    public ResponseEntity<ParticipationDto> addParticipationQuery(@Positive @PathVariable Long userId,
                                                                 @Positive @RequestParam(required = false) Long eventId) {
        log.info("Добавление запроса на участие пользователя id:{}, event id:{}", userId, eventId);
        return new ResponseEntity<>(participationService.addParticipationQuery(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationDto cancelParticipation(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отменить запрос на участие пользователя id:{}, запрос id:{}", userId, requestId);
        return participationService.cancelParticipation(userId, requestId);
    }
}