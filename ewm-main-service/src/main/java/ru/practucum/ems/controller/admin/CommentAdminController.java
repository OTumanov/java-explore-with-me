package ru.practucum.ems.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.service.CommentService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/comments/admin")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long commentId,
                                  HttpServletRequest request) {
        log.info("Удаление комментария #{} администратором", commentId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        commentService.deleteComment(commentId, clientIp, endpoint);
    }
}