package ru.practucum.ems.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.participation.ParticipationRequestDto;
import ru.practucum.ems.service.ParticipationService;

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
    public List<ParticipationRequestDto> getInfoAboutAllParticipation(@Positive @PathVariable Long userId) {
        log.info("Получение заявок пользователя #{} на участие в чужих событиях", userId);
        return participationService.getInfoAboutAllParticipation(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationQuery(@Positive @PathVariable Long userId,
                                                         @Positive @RequestParam(required = false) Long eventId) {
        log.info("Запрос на участие пользователя #{} в событие #{}", userId, eventId);
        return participationService.addParticipationQuery(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipation(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена запроса #{} на участие от пользователя #{}", requestId, userId);
        return participationService.cancelParticipation(userId, requestId);
    }
}