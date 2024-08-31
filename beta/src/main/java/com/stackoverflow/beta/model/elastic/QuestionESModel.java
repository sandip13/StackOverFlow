package com.stackoverflow.beta.model.elastic;

import com.stackoverflow.beta.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionESModel {
    private int id;
    private String title;
    private String content;
    private User askedBy;
    private int votes;
    private List<String> tags;
    private List<AnswerESModel> answers;
}
