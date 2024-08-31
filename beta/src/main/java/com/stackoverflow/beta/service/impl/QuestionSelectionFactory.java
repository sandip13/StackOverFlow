package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.service.TopQuestionSelectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class QuestionSelectionFactory {
    private static final String voteCriteria = "vote";
    private static final String timestamp = "timestamp";

    private final TopQuestionsByTimestamp topQuestionsByTimestamp;
    private final TopQuestionsByVotes topQuestionsByVotes;

    public QuestionSelectionFactory(TopQuestionsByTimestamp topQuestionsByTimestamp, TopQuestionsByVotes topQuestionsByVotes) {
        this.topQuestionsByTimestamp = topQuestionsByTimestamp;
        this.topQuestionsByVotes = topQuestionsByVotes;
    }

    public TopQuestionSelectionService selectingCriteriaObject(String criteria) {
        log.info("Selecting strategy for criteria: {}", criteria);
        if (voteCriteria.equalsIgnoreCase(criteria)) {
            return topQuestionsByVotes;
        } else if (timestamp.equalsIgnoreCase(criteria)) {
            return topQuestionsByTimestamp;
        } else {
            log.error("Invalid criteria provided: {}", criteria);
            throw new ValidationException("Invalid criteria: " + criteria, HttpStatus.BAD_REQUEST);
        }
    }
}
