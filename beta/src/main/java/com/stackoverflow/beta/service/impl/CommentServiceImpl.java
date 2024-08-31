package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.PostCommentInput;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.CommentRepository;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final ElasticSynchronizer elasticSynchronizer;

    public CommentServiceImpl(CommentRepository commentRepository,
                              AnswerRepository answerRepository,
                              UserRepository userRepository,
                              ElasticSynchronizer elasticSynchronizer) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.elasticSynchronizer = elasticSynchronizer;
    }

    @Override
    public Comment save(PostCommentInput commentInput) {
        Optional<User> optionalUser= userRepository.findById(commentInput.getUserId());
        if(optionalUser.isEmpty()){
            log.error("User with ID: {} not found", commentInput.getUserId());
            throw new ValidationException("User with given id doesn't exist " + commentInput.getUserId(), HttpStatus.BAD_REQUEST);
        }
        Optional<Answer> optionalAnswer= answerRepository.findById(commentInput.getAnswerId());
        if(optionalAnswer.isEmpty()){
            log.error("Answer with ID: {} not found", commentInput.getAnswerId());
            throw new ValidationException("Answer with given id doesn't exist " + commentInput.getAnswerId(),HttpStatus.BAD_REQUEST);
        }

        Comment comment = Comment.builder()
                .text(commentInput.getComment())
                .userId(optionalUser.get().getId())
                .answer(optionalAnswer.get())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment successfully saved with commentId={}", savedComment.getId());

        // Publishing the saved answer to Elasticsearch
        publishCommentsToES(savedComment);
        return comment;
    }

    @Async("threadPoolTaskExecutor")
    protected void publishCommentsToES(Comment comment){
        log.info("Publishing comment with ID: {} to Elasticsearch", comment.getId());
        try {
            elasticSynchronizer.syncCommentToES(comment);
        } catch(Exception e){
            //create an alert
            log.error("Exception while publishing comments to elastic for answerId=+"+comment.getAnswer().getId()+" ,exception="+e.getMessage());
        }
    }
}
