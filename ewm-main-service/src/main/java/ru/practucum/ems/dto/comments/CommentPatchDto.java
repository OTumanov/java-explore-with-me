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
    @Length(min = 10, max = 2048)
    private String text;
    private Long eventId;
}