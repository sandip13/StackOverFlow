package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.*;
import com.stackoverflow.beta.model.elastic.AnswerESModel;
import com.stackoverflow.beta.model.elastic.CommentESModel;
import com.stackoverflow.beta.model.elastic.QuestionESModel;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.CommentRepository;
import com.stackoverflow.beta.repository.elastic.QuestionESRepository;
import com.stackoverflow.beta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ElasticSynchronizer {

    private final QuestionESRepository questionESRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Autowired
    public ElasticSynchronizer(QuestionESRepository questionESRepository,
                               AnswerRepository answerRepository,
                               UserService userService,
                               CommentRepository commentRepository) {
        this.questionESRepository = questionESRepository;
        this.answerRepository = answerRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }


    public void syncQuestionDetailsToES(Question question, List<String> tags) throws IOException {
        log.info("Syncing questions - {}", question.getId());
        questionESRepository.save(toQuestionESModel(question, tags));
    }
    public QuestionESModel toQuestionESModel(Question question, List<String> tags) {

        QuestionESModel questionESModel = new QuestionESModel();
        questionESModel.setId( question.getId() );
        questionESModel.setTitle( question.getTitle() );
        questionESModel.setContent( question.getContent() );
        questionESModel.setVotes(question.getVotes());
        questionESModel.setAskedBy(userService.getUserById(question.getUserId()).get());
        questionESModel.setTags(tags);
        questionESModel.setAnswers(new ArrayList<>());
        return questionESModel;
    }

    public void syncAnswerToES(Answer answer) throws IOException {
        log.info("Syncing answer with - {}", answer.getId());
        Optional<QuestionESModel> optionalQuestionESModel = questionESRepository.findById(String.valueOf(answer.getQuestion().getId()));
        if(optionalQuestionESModel.isEmpty()){
            return;
        }
        QuestionESModel questionESModel = optionalQuestionESModel.get();
        List<AnswerESModel> answerESModels = questionESModel.getAnswers();
        if(CollectionUtils.isEmpty(answerESModels)){
            answerESModels = new ArrayList<>();
        }
        answerESModels.add(toAnswertoQuestionESModel(answer));
        questionESModel.setAnswers(answerESModels);
        questionESRepository.save(questionESModel);
    }

    public void syncCommentToES(Comment comment) throws IOException {
        log.info("Syncing comment with - {}", comment.getId());
        Optional<Answer> optionalAnswer = answerRepository.findById(comment.getAnswer().getId());
        Answer answer = optionalAnswer.get();
        Optional<QuestionESModel> optionalQuestionESModel = questionESRepository.findById(String.valueOf(answer.getQuestion().getId()));
        if(optionalQuestionESModel.isEmpty()){
            return;
        }
        QuestionESModel questionESModel = optionalQuestionESModel.get();
        List<AnswerESModel> answerESModels = questionESModel.getAnswers();
        for(AnswerESModel answerESModel : answerESModels){
            if(answerESModel.getId()==optionalAnswer.get().getId()){
                List<CommentESModel> commentESModels = answerESModel.getComments();
                if(CollectionUtils.isEmpty(commentESModels)){
                    commentESModels = new ArrayList<>();
                }
                commentESModels.add(toCommentEsModel(comment));
                answerESModel.setComments(commentESModels);
            }
            questionESModel.setAnswers(answerESModels);
        }
        if(CollectionUtils.isEmpty(answerESModels)){
            //alert setup
            log.error("Failed to sync comment for answerId="+answer.getId()+" and questionId="+answer.getQuestion().getId());
            return;
        }
        questionESRepository.save(questionESModel);
    }

    private CommentESModel toCommentEsModel(Comment comment) {
        CommentESModel commentESModel = new CommentESModel();
        commentESModel.setText(comment.getText());
        commentESModel.setId(comment.getId());
        return commentESModel;
    }

    private AnswerESModel toAnswertoQuestionESModel(Answer answer) {
        AnswerESModel answerESModel = new AnswerESModel();
        answerESModel.setContent(answer.getContent());
        answerESModel.setId(answer.getId());
        answerESModel.setVotes(answer.getVotes());
        answerESModel.setUserId(userService.getUserById(answer.getUserId()).get());
        if(null != answer.getMediaUrl()){
            answerESModel.setMediaUrl(answer.getMediaUrl());
        }
        return answerESModel;
    }

    public void syncQuestionDetailsToES(QuestionESModel msg) throws IOException {
        log.info("Syncing questions - {}", msg.getId());
        questionESRepository.save(msg);
    }
}
