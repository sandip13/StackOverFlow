package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.constant.Constants;
import com.stackoverflow.beta.model.ResultQuery;
import com.stackoverflow.beta.service.SearchService;
import com.stackoverflow.beta.utils.HelperFunctions;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Value("${api.elasticsearch.uri}")
    private String elasticSearchUri;

    @Value("${api.elasticsearch.search}")
    private String elasticSearchSearchPrefix;
    @Override
    public ResultQuery searchFromQuery(String query) throws IOException {
        String body = HelperFunctions.buildMultiIndexMatchBody(query);
        return executeHttpRequest(body);
    }

    /**
     * Fetch resultQuery from elastic engine for the given body
     *
     * @param body String
     * @return ResultQuery
     * @throws IOException IOException
     */
    private ResultQuery executeHttpRequest(String body) throws IOException{
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ResultQuery resultQuery = new ResultQuery();
            HttpPost httpPost = new HttpPost(HelperFunctions.buildSearchUri(elasticSearchUri
                    , "", elasticSearchSearchPrefix));
            httpPost.setHeader(Constants.CONTENT_ACCEPT, Constants.APP_TYPE);
            httpPost.setHeader(Constants.CONTENT_TYPE, Constants.APP_TYPE);
            try {
                httpPost.setEntity(new StringEntity(body, Constants.ENCODING_UTF8));
                HttpResponse response = (HttpResponse) httpClient.execute(httpPost);
                String message = EntityUtils.toString((HttpEntity) response);
                JSONObject myObject = new JSONObject(message);
                if(myObject.getJSONObject(Constants.HITS)
                        .getInt(Constants.TOTAL_HITS) != 0){
                    resultQuery
                            .setElements(myObject
                                    .getJSONObject(Constants.HITS)
                                    .getJSONArray(Constants.HITS)
                                    .toString());
                    resultQuery
                            .setNumberOfResults(myObject.getJSONObject(Constants.HITS)
                                    .getInt(Constants.TOTAL_HITS));
                    resultQuery.setTimeTook((float) ((double) myObject.getInt(Constants.TOOK) / Constants.TO_MS));
                } else {
                    resultQuery.setElements(null);
                    resultQuery.setNumberOfResults(0);
                    resultQuery.setTimeTook((float) ((double) myObject.getInt(Constants.TOOK) / Constants.TO_MS));
                }
            } catch (IOException | JSONException e) {
                log.error("Error while connecting to elastic engine --> {}", e.getMessage());
                resultQuery.setNumberOfResults(0);
            }

            return resultQuery;
        }
    }
}
