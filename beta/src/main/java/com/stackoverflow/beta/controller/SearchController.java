package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.constant.Constants;
import com.stackoverflow.beta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling search operations.
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService){
        this.searchService=searchService;
    }

    /**
     * Endpoint for searching based on a query.
     *
     * @param query The search query.
     * @return A ResponseEntity containing the search result or error message.
     */

    @GetMapping(Constants.SEARCH_QUERY + "/{" + Constants.QUERY + "}")
    public ResponseEntity<?> searchQuery(@PathVariable String query) {
        try{
            return new ResponseEntity<> (searchService.searchFromQuery(query.trim().toLowerCase()), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }

}
