package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Tags;

import java.util.List;

public interface TagsService {
    Tags getTagByName(String name);

    Tags createTag(Tags tag);
}
