package com.bug.bug_reporter.utility;

import com.bug.bug_reporter.model.Tag;
import com.bug.bug_reporter.repository.TagRepository;


public class Utility {
    public static Tag getOrCreateTag(String name, TagRepository tagRepository) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
    }

}
