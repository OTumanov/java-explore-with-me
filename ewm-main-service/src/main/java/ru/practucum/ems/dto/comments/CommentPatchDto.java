package ru.practucum.ems.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPatchDto {

    @NotNull
    private Long id;

    @Length(min = 10, max = 2048)
    private String text;
    private Long eventId;
}