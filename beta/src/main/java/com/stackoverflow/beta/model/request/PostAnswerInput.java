package com.stackoverflow.beta.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAnswerInput {
    private int questionId;
    private int userId;
    private String answer;
}
