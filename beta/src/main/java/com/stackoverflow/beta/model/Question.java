package com.stackoverflow.beta.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String title;

    @Column(length = 3000)
    private String content;
    private int votes;
    private int userId;
    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;


    /**
     * check for lock
     */
    public void upvote(){
        this.votes = this.votes + 1;
    }
    public void downVote(){
        this.votes = this.votes - 1;
    }


    public Question(String title, String content, User askedBy) {
        this.title = title;
        this.content = content;
        this.userId = askedBy.getId();
    }

    public Question(String title, String content, int askedBy) {
        this.title = title;
        this.content = content;
        this.userId = askedBy;
    }

}
