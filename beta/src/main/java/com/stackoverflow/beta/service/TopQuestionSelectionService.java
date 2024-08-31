package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;

import java.util.List;

public interface TopQuestionSelectionService {
    List<Question> selectTopQuestions(List<Question> questions, int searchCount);
}
