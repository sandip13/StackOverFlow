package com.stackoverflow.beta.consumer;

import com.stackoverflow.beta.model.elastic.QuestionESModel;
import com.stackoverflow.beta.service.impl.ElasticSynchronizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaMessageListener {

    @Autowired
    ElasticSynchronizer elasticSynchronizer;

    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
            attempts = "2",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
    )
    @KafkaListener(topics = "stackoverflow-demo1", groupId = "sof-group-1")
    public void consume(QuestionESModel msg){
        try {
            elasticSynchronizer.syncQuestionDetailsToES(msg);
            log.info("msg consumed {}",msg.toString());
        } catch(Exception e){
            //create an alert
            log.error("Exception while publishing to elastic for questionId=+"+msg.getId()+" ,exception="+e.getMessage());
        }
    }

    //dead letter topic
    @DltHandler
    public void listenDLT(QuestionESModel questionESModel){
        log.info("DLT received: {}", questionESModel.getId());
    }
}
