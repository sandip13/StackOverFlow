package com.stackoverflow.beta.model.elastic;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentESModel {
    private int id;
    private int userId;
    private String text;
}
