package ru.practucum.ems.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPatchDto {
    @Length(min = 10, max = 2048, message = "Длина комментария должна быть не менее 10 символов и не должна превышать 2048 символов")
    private String text;
    private Long eventId;
}