package com.stackoverflow.beta.repository.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.elastic.QuestionESModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class QuestionESRepository {

    private final ElasticsearchClient elasticsearchClient;

    public QuestionESRepository(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void save(QuestionESModel question) throws IOException {
        elasticsearchClient.index(i -> i
                .index("questions")
                .id(String.valueOf(question.getId()))
                .document(question)
        );
    }

    public Optional<QuestionESModel> findById(String id) throws IOException {
        GetResponse<QuestionESModel> response = elasticsearchClient.get(g -> g
                .index("questions")
                .id(id), QuestionESModel.class
        );
        return response.found() ? Optional.ofNullable(response.source()) : Optional.empty();
    }

    // Implement other CRUD methods similarly...
}
