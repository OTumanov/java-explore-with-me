package ru.practucum.ems.dto.comments;

import lombok.*;
import ru.practucum.ems.dto.events.EventShortDto;
import ru.practucum.ems.dto.users.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String text;
    private UserShortDto owner;
    private EventShortDto event;
    private String date;
}