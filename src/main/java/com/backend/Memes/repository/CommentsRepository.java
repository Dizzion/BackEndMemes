package com.backend.Memes.repository;

import com.backend.Memes.model.Comments;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comments, String> {
    //    Comments findByUser(User user);
    List<Comments> findByMemeId(String memeId);
}
