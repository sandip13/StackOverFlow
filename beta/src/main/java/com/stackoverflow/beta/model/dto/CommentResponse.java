package com.stackoverflow.beta.model.dto;

import com.stackoverflow.beta.model.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private int id;
    private int userId;
    private String text;
    private Answer answer;
}
