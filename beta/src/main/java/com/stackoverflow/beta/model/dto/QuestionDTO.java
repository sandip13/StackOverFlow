package com.stackoverflow.beta.model.dto;

import com.stackoverflow.beta.model.User;
import lombok.Data;


@Data
public class QuestionDTO {
    private int id;
    private String title;
    private String content;
    private User askedBy;
}
