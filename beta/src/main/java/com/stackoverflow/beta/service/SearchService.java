package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.ResultQuery;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public interface SearchService {
    ResultQuery searchFromQuery(String query) throws IOException;

}
