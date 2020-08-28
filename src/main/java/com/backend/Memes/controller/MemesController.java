package com.backend.Memes.controller;

import com.backend.Memes.model.Comments;
import com.backend.Memes.model.Memes;
import com.backend.Memes.model.User;
import com.backend.Memes.repository.CommentsRepository;
import com.backend.Memes.repository.MemesRepository;
import com.backend.Memes.repository.RoleRepository;
import com.backend.Memes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// adding CORS and making this controller a rest controller starting at /api/v1
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/v1")
public class MemesController {

    //    wiring repositories to run through their imported functionality
    @Autowired
    private MemesRepository memesRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    //    get route for home page
    @GetMapping("/memes")
    public ResponseEntity<List<Memes>> getTrending() {
        try {
//            creating arrays for the meme and trending memes
            List<Memes> memes = new ArrayList<Memes>();
            List<Memes> trendMemes = new ArrayList<Memes>();
//            use repository to find each meme and push it into memes
            memesRepository.findAll().forEach(memes::add);

            if (memes.isEmpty()) {
//                if empty end get with no content response
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
//            loop over each meme in memes and if isTrending is true add it to trendMemes
            memes.forEach(meme -> {
                if (meme.isTrending()) {
                    trendMemes.add(meme);
                }
            });
//            if trendMemes is empty or less than one page remove excess from memes and return it
            if (trendMemes.isEmpty() || trendMemes.size() < 12) {
                while (memes.size() > 24) {
                    memes.remove(24);
                }
                return new ResponseEntity<>(memes, HttpStatus.OK);
            }
            return new ResponseEntity<>(trendMemes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    Get mapping for searching based on hashtags tied to memes
    @GetMapping("/memes/search/{hashtag}")
    public ResponseEntity<List<Memes>> getMemesByHashTag(@PathVariable("hashtag") String hashTag) {
        try {
//            create lists for each meme set
            List<Memes> memesList = memesRepository.findAll();
            if (hashTag == "all" || hashTag == "All" || hashTag == "ALL") {
                if (memesList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                return new ResponseEntity<>(memesList, HttpStatus.OK);
            }
            List<Memes> _meme = new ArrayList<Memes>();
//            loop over all memes to see if they contain the searched hashtag
            memesList.forEach(meme -> {
                if (meme.getHashTags().contains(hashTag)) {
                    _meme.add(meme);
                }
            });
            if (_meme.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(_meme, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    get mapping for individual memes
    @GetMapping("/memes/{id}")
    public ResponseEntity<Memes> getMemeById(@PathVariable("id") String id) {
//        grab meme ref by id
        Optional<Memes> memeData = memesRepository.findById(id);
//        check is anything is there
        if (memeData.isPresent()) {
            return new ResponseEntity<>(memeData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //    Post mapping for adding a comment to the individual memes
    @PostMapping("/memes/{id}")
    public ResponseEntity<Comments> createComment(@PathVariable("id") String id, @RequestBody Comments comment) {
        try {
//            save new comment and assign it to be added to the meme
            Comments _comment = commentsRepository.save(new Comments(comment.getBody(), comment.getUserPosted(), id));
            Memes meme = memesRepository.findById(id).get();
//            get the current list of comments on this meme
            List<Comments> coms = meme.getComments();
//            add the comment set the updated meme and save
            coms.add(_comment);
            meme.setComments(coms);
            memesRepository.save(meme);
            return new ResponseEntity<>(_comment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    Post mapping for adding a meme muse be logged in to access
    @PostMapping("/memes")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Memes> createMeme(@RequestBody Memes meme) {
        try {
//            save new meme to the database
            Memes _meme = memesRepository.save(new Memes(meme.getUrl(), meme.getHashTags(), meme.getDisLikes(), meme.getLikes(), meme.isTrending()));
//            get user posting new meme
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            find user in data base based on username
            User user  = userRepository.findByUsername(auth.getName()).get();
            List<Memes> userMemes = user.getUserMemes();
//            add new meme to the list of user Memes
            userMemes.add(_meme);
            user.setUserMemes(userMemes);
//            save the suer with the new list
            userRepository.save(user);
            return new ResponseEntity<>(_meme, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/memes/likes/{id}")
    public ResponseEntity<Memes> updateLikesMemeById(@PathVariable("id") String id, @RequestBody Memes meme) {
//        find the pointer of the memedata from the repository based on id
        Optional<Memes> memeData = memesRepository.findById(id);

        if (memeData.isPresent()) {
//            get the meme data from the pointer and save the editable value
            Memes _meme = memeData.get();
            _meme.setLikes(meme.getLikes());
            return new ResponseEntity<>(memesRepository.save(_meme), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/memes/disLikes/{id}")
    public ResponseEntity<Memes> updateDisLikesMemeById(@PathVariable("id") String id, @RequestBody Memes meme) {
//        find the pointer of the memedata from the repository based on id
        Optional<Memes> memeData = memesRepository.findById(id);

        if (memeData.isPresent()) {
//            get the meme data from the pointer and save the editable value
            Memes _meme = memeData.get();
            _meme.setDisLikes(meme.getDisLikes());
            return new ResponseEntity<>(memesRepository.save(_meme), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //    update meme based on its id
    @PutMapping("/memes/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Memes> updateMemeById(@PathVariable("id") String id, @RequestBody Memes meme) {
//        find the pointer of the memedata from the repository based on id
        Optional<Memes> memeData = memesRepository.findById(id);

        if (memeData.isPresent()) {
//            get the meme data from the pointer and save the editable value
            Memes _meme = memeData.get();
            _meme.setHashTags(meme.getHashTags());
            _meme.setTrending(meme.isTrending());
            return new ResponseEntity<>(memesRepository.save(_meme), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //    only moderators and admins can delete memes
    @DeleteMapping("/memes/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteGameById(@PathVariable("id") String id) {
        try {
//            delete comments associated with said meme
            List<Comments> coms = commentsRepository.findByMemeId(id);
            coms.forEach(com -> {
                commentsRepository.delete(com);
            });
//            delete the meme
            memesRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}