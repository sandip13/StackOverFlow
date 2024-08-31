package com.stackoverflow.beta.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 3000)
    private String content;
    private int votes;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    private int userId;
    private String mediaUrl;

    //todo try to make variable private
    @JsonManagedReference
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    /**
     * check for lock
     */
    public void upvote(){
        this.votes = this.votes + 1;
    }
    public void downVote(){
        this.votes = this.votes - 1;
    }
}
