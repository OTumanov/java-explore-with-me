package ru.practucum.ems.service;

import ru.practucum.ems.dto.comments.CommentPatchDto;
import ru.practucum.ems.dto.comments.CommentPostDto;
import ru.practucum.ems.dto.comments.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto postComment(CommentPostDto comment, Long userId, String clientIp, String endpoint);

    CommentResponseDto patchComment(CommentPatchDto comment, Long userId, String clientIp, String endpoint);

    void deleteComment(Long commentId, String clientIp, String endpoint);

    void deleteComment(Long commentId, Long userId, String clientIp, String endpoint);

    CommentResponseDto findCommentById(Long commentId, String clientIp, String endpoint);

    List<CommentResponseDto> findCommentsByEventId(Long eventId, String clientIp, String endpoint);
}