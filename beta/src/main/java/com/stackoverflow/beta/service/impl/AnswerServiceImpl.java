package com.stackoverflow.beta.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.PostAnswerInput;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.AnswerService;
import com.stackoverflow.beta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ElasticSynchronizer elasticSynchronizer;
    private final StorageService s3Service;

    public AnswerServiceImpl(AnswerRepository answerRepository,
                             QuestionRepository questionRepository,
                             UserService userService,
                             ElasticSynchronizer elasticSynchronizer,
                             StorageService s3Service) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.elasticSynchronizer = elasticSynchronizer;
        this.s3Service = s3Service;
    }

    @Override
    public Answer save(PostAnswerInput answerInput) {

        Optional<Question> optionalQuestion= questionRepository.findById(answerInput.getQuestionId());
        if(optionalQuestion.isEmpty()){
            throw new ValidationException("Question with given id doesn't exist " + answerInput.getQuestionId(), HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser= userService.getUserById(answerInput.getUserId());
        if(optionalUser.isEmpty()){
            throw new ValidationException("UserId"+ answerInput.getUserId()+" doesn't exist ",HttpStatus.BAD_REQUEST);
        }
        Question question = optionalQuestion.get();
        User user = optionalUser.get();
        Answer answer = Answer.builder()
                .userId(user.getId())
                .question(question)
                .content(answerInput.getAnswer())
                .build();

        // Saving the Answer object to the repository
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer successfully saved with answerId={}", savedAnswer.getId());


        // Publishing the saved answer to Elasticsearch
        publishAnswerInQuestionToES(savedAnswer);
        return savedAnswer;
    }

    @Async("threadPoolTaskExecutor")
    protected void publishAnswerInQuestionToES(Answer answer){
        try {
            elasticSynchronizer.syncAnswerToES(answer);
        } catch(Exception e){
            //create an alert
            log.error("Exception while publishing answer to elastic for questionId=+"+answer.getQuestion().getId()+" ,exception="+e.getMessage());
        }
    }

    @Override
    public Answer saveWithMedia(MultipartFile file, String json) {
        PostAnswerInput answerInput;
        try {
            ObjectMapper mapper = new ObjectMapper();
            answerInput = mapper.readValue(json, PostAnswerInput.class);
        } catch(JsonProcessingException e){
            throw new ValidationException("Exception while parsing json input to answerInput");
        }
        String mediaUrl = s3Service.uploadFile(file);
        Optional<Question> optionalQuestion= questionRepository.findById(answerInput.getQuestionId());
        if(optionalQuestion.isEmpty()){
            throw new ValidationException("Question with given id doesn't exist " + answerInput.getQuestionId(), HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser= userService.getUserById(answerInput.getUserId());
        if(optionalUser.isEmpty()){
            throw new ValidationException("User with given id doesn't exist " + answerInput.getUserId(), HttpStatus.BAD_REQUEST);
        }
        Question question = optionalQuestion.get();
        User user = optionalUser.get();
        Answer answer = Answer.builder()
                .userId(user.getId())
                .question(question)
                .content(answerInput.getAnswer())
                .mediaUrl(mediaUrl)
                .build();

        // Saving the Answer object to the repository
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer successfully saved with answerId={}", savedAnswer.getId());

        // Publishing the saved answer to Elasticsearch
        publishAnswerInQuestionToES(savedAnswer);
        return savedAnswer;
    }

    @Override
    public Answer getAnswerById(int id) {
        return answerRepository.findById(id).orElseThrow(()->new ValidationException("Answer not found", HttpStatus.BAD_REQUEST));
    }
}
