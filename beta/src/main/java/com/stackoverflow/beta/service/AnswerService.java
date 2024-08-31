package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.dto.AnswerResponse;
import com.stackoverflow.beta.model.request.PostAnswerInput;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnswerService {
    Answer save(PostAnswerInput answerInput);

    Answer saveWithMedia(MultipartFile file, String postAnswerInput);

    Answer getAnswerById(int questionId);
}
