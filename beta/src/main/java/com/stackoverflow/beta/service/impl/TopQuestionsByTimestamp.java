package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.service.TopQuestionSelectionService;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TopQuestionsByTimestamp implements TopQuestionSelectionService {

    public List<Question> selectTopQuestions(List<Question> questions, int searchCount) {
        questions.sort((b, a) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        return questions.subList(0,Math.min(questions.size(),searchCount));
    }
}
