package com.stackoverflow.beta.model.dto;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Comment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnswerResponse {
    private Answer answer;
    private List<Comment> comments;
}
