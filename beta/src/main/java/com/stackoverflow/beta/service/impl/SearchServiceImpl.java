package com.stackoverflow.beta.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.stackoverflow.beta.model.ResultQuery;
import com.stackoverflow.beta.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Value("${api.elasticsearch.uri}")
    private String elasticSearchUri;

    public SearchServiceImpl(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public ResultQuery searchFromQuery(String query) {
        try {
            // Build the search request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("questions")  // Specify your index name here
                    .query(q -> q
                            .queryString(qs -> qs
                                    .query(query)   // The query string to search for
                                    .defaultField("*")  // Search across all fields
                            )
                    )
            );

            // Execute the search
            SearchResponse<Object> searchResponse = elasticsearchClient.search(searchRequest, Object.class);

            // Process the results
            List<String> hits = searchResponse.hits().hits().stream()
                    .map(Hit::source).filter(Objects::nonNull)
                    .map(Object::toString).toList();

            ResultQuery resultQuery = new ResultQuery();
            resultQuery.setElements(hits.toString());
            resultQuery.setNumberOfResults(hits.size());

            // Set the time took in seconds
            resultQuery.setTimeTook(searchResponse.took() / 1000.0f);

            return resultQuery;

        } catch (IOException e) {
            log.error("Error while connecting to Elasticsearch --> {}", e.getMessage());
            ResultQuery resultQuery = new ResultQuery();
            resultQuery.setNumberOfResults(0);
            return resultQuery;
        }
    }

}
