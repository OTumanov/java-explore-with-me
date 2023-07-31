package ru.practucum.ems.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practucum.ems.dto.comments.CommentResponseDto;
import ru.practucum.ems.service.CommentService;


import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentResponseDto findCommentById(@Positive
                                              @PathVariable @NotNull Long commentId,
                                              HttpServletRequest request) {
        log.info("Просмотр комментария id:{}", commentId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        return commentService.findCommentById(commentId, clientIp, endpoint);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentResponseDto> findCommentsByEventId(@Positive
                                                          @PathVariable @NotNull Long eventId,
                                                          HttpServletRequest request) {
        log.info("Просмотр комментариев события id:{}", eventId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        return commentService.findCommentsByEventId(eventId, clientIp, endpoint);
    }
}