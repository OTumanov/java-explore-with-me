package ru.practucum.ems.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practucum.ems.service.CommentService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/comment/admin")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public void deleteCommentById(@PathVariable Long commentId,
                                  @RequestParam("userId") Long userId,
                                  HttpServletRequest request) {
        log.info("Удаление комментария id:{} администратором id:{}", commentId, userId);
        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        commentService.deleteComment(commentId, userId, clientIp, endpoint);
    }
}