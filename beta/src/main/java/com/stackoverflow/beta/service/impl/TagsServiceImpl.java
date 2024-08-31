package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.Tags;
import com.stackoverflow.beta.repository.TagRepository;
import com.stackoverflow.beta.service.TagsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TagsServiceImpl implements TagsService {
    private final TagRepository tagRepository;

    public  TagsServiceImpl(TagRepository tagRepository){
        this.tagRepository=tagRepository;
    }

    @Override
    public Tags getTagByName(String name) {
        log.info("Fetching tag by name: {}", name);
        return tagRepository.findByName(name);
    }

    @Override
    public Tags createTag(Tags tag) {
        log.info("Creating new tag with name: {}", tag.getName());
        return tagRepository.save(tag);
    }
}
