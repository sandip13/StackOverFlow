package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.*;
import com.stackoverflow.beta.model.dto.TopQuestionResponse;
import com.stackoverflow.beta.model.elastic.QuestionESModel;
import com.stackoverflow.beta.model.request.PostQuestionInput;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.repository.QuestionTagRepository;
import com.stackoverflow.beta.service.*;
import com.stackoverflow.beta.utils.CustomPriorityQueue;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ElasticSynchronizer elasticSynchronizer;
    private final UserService userService;
    private final QuestionTagRepository questionTagRepository;
    private final TagsService tagsService;
    private final CustomPriorityQueue priorityQueue = CustomPriorityQueue.getCustomPriorityQueue();

    private final  KafkaTemplate<String,Object> template;

    @PostConstruct
    private void loadCache(){
        List<Question> questions = questionRepository.findAll();
        priorityQueue.addAll(questions);
    }

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               ElasticSynchronizer elasticSynchronizer,
                               UserService userService,
                               QuestionTagRepository questionTagRepository,
                               TagsService tagsService, KafkaTemplate<String, Object> template) {
        this.questionRepository = questionRepository;
        this.elasticSynchronizer = elasticSynchronizer;
        this.userService = userService;
        this.questionTagRepository = questionTagRepository;
        this.tagsService = tagsService;
        this.template = template;
    }

    @Override
    public Question save(PostQuestionInput postQuestionInput) {
        if(!userService.isUserExist(postQuestionInput.getUserId()))
        {
            log.error("User with ID: {} does not exist.", postQuestionInput.getUserId());
            throw new ValidationException("User not registered!!", HttpStatus.NOT_FOUND);
        }
        if(isQuestionExists(postQuestionInput)){
            log.error("Question with title: '{}' already exists.", postQuestionInput.getTitle());
            throw new ValidationException("Question already exist", HttpStatus.BAD_REQUEST);
        }
       List<Integer> tagIdList = getTagIds(postQuestionInput.getTags());

        Question question = Question.builder()
                .userId(postQuestionInput.getUserId())
                .title(postQuestionInput.getTitle())
                .content(postQuestionInput.getContent())
                .build();
        // Save the new question to the repository
        Question savedQuestion = questionRepository.save(question);
        log.info("Question successfully saved with ID: {}", savedQuestion.getId());

        for(Integer tagId : tagIdList){
            QuestionTags questionTags = QuestionTags.builder().tagId(tagId).questionId(savedQuestion.getId()).build();
            questionTagRepository.save(questionTags);
        }
        //updating cache
        priorityQueue.add(savedQuestion);
        //sending to kafka
        QuestionESModel questionESModel = elasticSynchronizer.toQuestionESModel(question,postQuestionInput.getTags());
        //publishing to kafka
        sendMessageToTopic(questionESModel);

        //async thread to sync data to els
        //publishQuestionToES(savedQuestion, postQuestionInput.getTags());
        return savedQuestion;
    }

    public void sendMessageToTopic(QuestionESModel message){
        CompletableFuture<SendResult<String, Object>> future = template.send("stackoverflow-demo1", message);
        future.whenComplete((result,ex)->{
            if (ex == null) {
                System.out.println("Sent message=[" + message.toString() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        message.toString() + "] due to : " + ex.getMessage());
            }
        });

    }

    private boolean isQuestionExists(PostQuestionInput postQuestionInput) {
        int count = questionRepository.countByTitleContentAndUserId(postQuestionInput.getTitle(),postQuestionInput.getContent(),postQuestionInput.getUserId());
        return count>0;
    }

    private List<Integer> getTagIds(List<String> questionTags) {
        List<Integer> tagIdList = new ArrayList<>();
        for(String inputTag: questionTags){
            Tags tag = tagsService.getTagByName(inputTag);
            if(Objects.isNull(tag)){
                Tags savedTag= Tags.builder().name(inputTag).build();
                tag = tagsService.createTag(savedTag);
            }
            tagIdList.add(tag.getId());
        }
        return tagIdList;
    }


    @Override
    public List<Question> getQuestionsByTag(String tagInput) {
        log.info("Fetching questions for tag: {}", tagInput);
        List<Question> allQuestion = new ArrayList<>();
        Tags tag = tagsService.getTagByName(tagInput);
        if(Objects.isNull(tag)){
            log.error("Tag with name: '{}' does not exist.", tagInput);
            throw new ValidationException("Given tag doesn't exist", HttpStatus.BAD_REQUEST);
        }
        List<QuestionTags> questionTagsList = questionTagRepository.findByTagId(tag.getId());
        for(QuestionTags questionTags: questionTagsList){
            Optional<Question> optionalQuestion = questionRepository.findById(questionTags.getQuestionId());
            Question question= optionalQuestion.get();
            allQuestion.add(question);
        }
        log.info("Successfully fetched {} questions for tag: {}", allQuestion.size(), tagInput);
        return allQuestion;
    }

    @Override
    public Question getQuestionById(int id) {
        log.info("Fetching question with ID: {}", id);
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if(optionalQuestion.isEmpty())
        {
            log.error("Question with ID: {} does not exist.", id);
            throw new ValidationException("Question not found!!",HttpStatus.NOT_FOUND);
        }
        Question question = optionalQuestion.get();
        log.info("Successfully fetched question with ID: {}", id);
        return question;
    }

    @Override
    public List<TopQuestionResponse> getTopQuestions(String criteria, int count) {
        log.info("Fetching top questions based on criteria: {}", criteria);
        List<TopQuestionResponse> topQuestions = new ArrayList<>();
        List<Question> tempQuestions = new ArrayList<>();
        while (!priorityQueue.isEmpty()){
            tempQuestions.add(priorityQueue.poll());
        }
        priorityQueue.addAll(tempQuestions);

        for(int i=tempQuestions.size()-1,j=0; j < count && i>=0 ; i--,j++){
            Question question = tempQuestions.get(i);
            TopQuestionResponse topQuestionResponse = new TopQuestionResponse(question.getTitle(), question.getVotes(), question.getUserId());
            topQuestions.add(topQuestionResponse);
        }
        return topQuestions;
    }


    @Async("threadPoolTaskExecutor")
    protected void publishQuestionToES(Question question, List<String> tags){
        try {
            elasticSynchronizer.syncQuestionDetailsToES(question, tags);
        } catch(Exception e){
            //create an alert
            log.error("Exception while publishing to elastic for questionId=+"+question.getId()+" ,exception="+e.getMessage());
        }
    }


}
