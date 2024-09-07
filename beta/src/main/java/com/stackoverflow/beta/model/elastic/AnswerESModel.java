package com.stackoverflow.beta.model.elastic;

import com.stackoverflow.beta.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerESModel {
    private int id;
    private String content;
    private int votes;
    private User user;
    private List<CommentESModel> comments;
    private String mediaUrl;
}
