package ru.practucum.ems.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.dto.comments.CommentPatchDto;
import ru.practucum.ems.dto.comments.CommentPostDto;
import ru.practucum.ems.dto.comments.CommentResponseDto;
import ru.practucum.ems.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto postComment(@Valid @RequestBody CommentPostDto dto,
                                          @RequestParam("userId") Long userId,
                                          HttpServletRequest request) {
        log.info("Добавление нового комментария:{} пользователем id:{}", dto, userId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        return commentService.postComment(dto, userId, clientIp, endpoint);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto patchComment(@Validated @RequestBody CommentPatchDto dto,
                                           @RequestParam("userId") Long userId,
                                           HttpServletRequest request) {
        log.info("Обновление комментария id:{}, {} пользователем id:{}", dto.getId(), dto, userId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        return commentService.patchComment(dto, userId, clientIp, endpoint);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId,
                              @RequestParam("userId") Long userId,
                              HttpServletRequest request) {
        log.info("Удаление комментария id:{} пользователем id:{}", commentId, userId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        commentService.deleteComment(commentId, userId, clientIp, endpoint);
    }
}