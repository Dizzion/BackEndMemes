package com.backend.Memes.repository;


import com.backend.Memes.model.Memes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemesRepository extends MongoRepository<Memes, String> {
    Memes findMemesByHashTags(String hashTags);
}
