package com.stackoverflow.beta.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentInput {
    private int answerId;
    private String comment;
    private int userId;
}
