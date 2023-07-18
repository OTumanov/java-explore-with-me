package ru.practicum.mnsvc.controller.privateController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mnsvc.dto.participation.ParticipationDto;
import ru.practicum.mnsvc.service.ParticipationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationDto> getInfoAboutAllParticipation(@PathVariable Long userId) {
        log.info("get info about all participation user id:{}", userId);
        return participationService.getInfoAboutAllParticipation(userId);
    }

    @PostMapping
    public ParticipationDto addParticipationQuery(@PathVariable Long userId,
                                                  @RequestParam Long eventId) {
        log.info("add participation query user id:{}, event id:{}", userId, eventId);
        return participationService.addParticipationQuery(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationDto cancelParticipation(@PathVariable Long userId,
                                                @PathVariable Long requestId) {
        log.info("cancel participation user id:{}, request id:{}", userId, requestId);
        return participationService.cancelParticipation(userId, requestId);
    }
}