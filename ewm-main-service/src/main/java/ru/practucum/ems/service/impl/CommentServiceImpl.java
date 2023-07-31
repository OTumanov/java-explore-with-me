package ru.practucum.ems.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.sd.dto.UtilDto;
import ru.practicum.ssc.StatsClient;
import ru.practucum.ems.dto.comments.CommentPatchDto;
import ru.practucum.ems.dto.comments.CommentPostDto;
import ru.practucum.ems.dto.comments.CommentResponseDto;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.dto.users.UserShortDto;
import ru.practucum.ems.exceptions.ForbiddenException;
import ru.practucum.ems.exceptions.NotFoundException;
import ru.practucum.ems.mapper.CommentMapper;
import ru.practucum.ems.mapper.EventMapper;
import ru.practucum.ems.mapper.UserMapper;
import ru.practucum.ems.model.Comment;
import ru.practucum.ems.model.Event;
import ru.practucum.ems.model.ParticipationState;
import ru.practucum.ems.model.User;
import ru.practucum.ems.repository.CommentRepository;
import ru.practucum.ems.repository.EventRepository;
import ru.practucum.ems.repository.ParticipationRepository;
import ru.practucum.ems.repository.UserRepository;
import ru.practucum.ems.service.CommentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practucum.ems.utils.Util.matchIntValueByEventId;
import static ru.practucum.ems.utils.Util.matchLongValueByEventId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final String APP_NAME = "EWM-MAIN-SERVICE";

    private final ParticipationRepository participationRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatsClient client;

    @Override
    public CommentResponseDto findCommentById(Long commentId, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        Comment comment = checkComment(commentId);
        return mapToCommentResponseDto(comment);
    }

    @Override
    public List<CommentResponseDto> findCommentsByEventId(Long eventId, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return mapToCommentResponseDtoList(comments);
    }

    private void updateComment(Comment comment, CommentPatchDto dto) {
        if (dto.getText() != null && !dto.getText().isBlank()) {
            comment.setText(dto.getText());
        }
    }

    @Override
    @Transactional
    public CommentResponseDto postComment(CommentPostDto dto, Long userId, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        User owner = checkUser(userId);
        Event event = checkEvent(dto);
        Comment comment = CommentMapper.toModel(dto, owner, event);
        comment = commentRepository.save(comment);
        return mapToCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto patchComment(CommentPatchDto dto, Long userId, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        Comment comment = checkComment(dto);
        checkOwnerComment(dto, userId, comment);
        updateComment(comment, dto);
        return mapToCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        checkComment(id);
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId, String clientIp, String endpoint) {
        client.hitRequest(EventMapper.endpointHitDto(APP_NAME, clientIp, endpoint));
        Comment comment = checkComment(commentId);
        Long ownerId = comment.getOwner().getId();
        checkOwnerComment(ownerId, userId, comment);
        commentRepository.deleteById(commentId);
    }

    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        UserShortDto ownerDto = UserMapper.toUserShortDto(comment.getOwner());
        Event event = comment.getEvent();
        Long views = event.getViews();
        Integer confirmedRequests = participationRepository.getConfirmedRequests(event.getId(), ParticipationState.CONFIRMED);
        EventShortDto eventDto = EventMapper.toEventShortDto(event, confirmedRequests, views);
        return CommentMapper.toResponseDto(comment, ownerDto, eventDto);
    }

    private List<CommentResponseDto> mapToCommentResponseDtoList(List<Comment> comments) {
        List<Event> events = comments.stream().map(Comment::getEvent).collect(Collectors.toList());
        List<EventShortDto> eventDtos = prepareDataAndGetEventShortDtoList(events);

        return comments.stream().map(comment -> {
            UserShortDto ownerDto = UserMapper.toUserShortDto(comment.getOwner());
            long eventId = comment.getEvent().getId();
            EventShortDto eventDto = eventDtos.stream()
                    .filter(eventShortDto -> eventShortDto.getId().equals(eventId))
                    .findFirst().orElseThrow(() -> new NotFoundException("Событие не найдено"));
            return CommentMapper.toResponseDto(comment, ownerDto, eventDto);
        }).collect(Collectors.toList());
    }

    private List<EventShortDto> prepareDataAndGetEventShortDtoList(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<UtilDto> confirmedReqEventIdRelations = participationRepository.countParticipationByEventIds(eventIds, ParticipationState.CONFIRMED);
        List<UtilDto> viewsEventIdRelations = new ArrayList<>();
        for (Event event : events) {
            UtilDto eventDto = UtilDto.builder()
                    .count(event.getViews())
                    .entityId(event.getId())
                    .build();
            viewsEventIdRelations.add(eventDto);
        }

        return events.stream()
                .map((Event event) -> EventMapper.toEventShortDto(
                        event,
                        matchIntValueByEventId(confirmedReqEventIdRelations, event.getId()),
                        matchLongValueByEventId(viewsEventIdRelations, event.getId())))
                .collect(Collectors.toList());
    }

    private Comment checkComment(CommentPatchDto dto) {
        return commentRepository.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Коммнетарий не найден"));
    }

    private Comment checkComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Коммнетарий не найден"));
    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Event checkEvent(CommentPostDto dto) {
        return eventRepository.findById(dto.getEventId()).orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private void checkOwnerComment(CommentPatchDto dto, Long userId, Comment comment) {
        if (!comment.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Этот пользователь не создавал такой комментарий и не является администратором");
        }
    }

    private void checkOwnerComment(Long ownerId, Long userId, Comment comment) {
        if (!ownerId.equals(userId)) {
            throw new ForbiddenException("Пользователь id:" + userId
                    + " не является создателем комментария или администратором");
        }
    }
}