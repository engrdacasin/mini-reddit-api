package com.example.JuniorInterview.Repository;

import com.example.JuniorInterview.Model.Comment;
import com.example.JuniorInterview.Model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll();
    Optional<Post> findById(Integer postId);
    Post save(Post post);
    void deleteById(Integer postId);

    Comment addComment(Integer postId, Comment comment);
    void deleteComment(Integer postId, Integer commentId);

    List<Post> search(String q, String sortBy, String order, int page, int size);
}