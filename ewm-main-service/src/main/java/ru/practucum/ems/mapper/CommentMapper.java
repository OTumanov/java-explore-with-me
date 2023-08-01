package ru.practucum.ems.mapper;

import lombok.NoArgsConstructor;
import ru.practucum.ems.dto.comments.CommentPostDto;
import ru.practucum.ems.dto.comments.CommentResponseDto;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.dto.users.UserShortDto;
import ru.practucum.ems.model.Comment;
import ru.practucum.ems.model.Event;
import ru.practucum.ems.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
public final class CommentMapper {
    public static Comment toModel(CommentPostDto dto, User owner, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .owner(owner)
                .event(event)
                .date(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toResponseDto(Comment model, UserShortDto ownerDto, EventShortDto eventDto) {
        return CommentResponseDto.builder()
                .id(model.getId())
                .text(model.getText())
                .owner(ownerDto)
                .event(eventDto)
                .date(DateTimeMapper.toString(model.getDate()))
                .build();
    }
}