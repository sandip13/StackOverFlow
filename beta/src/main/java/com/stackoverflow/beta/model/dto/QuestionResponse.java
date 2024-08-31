package com.stackoverflow.beta.model.dto;

import com.stackoverflow.beta.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private Question question;
    private List<AnswerResponse> answers;
    private List<String> tags;
}
