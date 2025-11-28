package com.example.JuniorInterview.Controller;

import com.example.JuniorInterview.Model.Comment;
import com.example.JuniorInterview.Model.Post;
import com.example.JuniorInterview.Service.PostService;
import com.example.JuniorInterview.dto.CreateCommentRequest;
import com.example.JuniorInterview.dto.CreatePostRequest;
import com.example.JuniorInterview.dto.UpdateCommentRequest;
import com.example.JuniorInterview.dto.UpdatePostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService service;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts(  @RequestParam(required = false) String q,
                                                 @RequestParam(defaultValue = "createdAt") String sortBy,
                                                 @RequestParam(defaultValue = "desc") String order,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size ) {
        List<Post> posts;
        if (q != null && !q.isBlank()) {
            posts = service.search(q, sortBy, order, page, size);
        } else {
            posts = service.getAllPaginated(sortBy, order, page, size);
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Integer postId) {
        Post post = service.getPost(postId);
        if (post.getPostId() > 0)
            return new ResponseEntity<>(post, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer postId) {
        Post post = service.getPost(postId);
        if (post.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", post.getImageType())
                .header("Content-Disposition", "attachment; filename=\"" + post.getImageName() + "\"")
                .body(post.getImageData());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Post> create(@Validated @RequestPart("post") CreatePostRequest req,
                                       @RequestPart(value = "image", required = false) MultipartFile image,
                                       Authentication auth) {
        Post created = service.createPost(req, image, auth.getName());
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> update(@PathVariable Integer postId,
                                       @Validated @RequestBody UpdatePostRequest request,
                                       Authentication auth) {
        Post updated = service.updatePost(postId, request, auth.getName());
        return ResponseEntity.status(201).body(updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Integer postId, Authentication auth) {
        service.deletePost(postId, auth.getName());
        return ResponseEntity.noContent().build();
    }
    //comments
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer postId,
                                              @Validated @RequestBody CreateCommentRequest request, Authentication auth) {
        Comment c = service.addComment(postId, request, auth.getName());
        return ResponseEntity.status(201).body(c);
    }
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> update(@PathVariable Integer postId, @PathVariable Integer commentId, @Validated @RequestBody UpdateCommentRequest request, Authentication auth) {
        Comment updated = service.updateComment(postId, commentId, request, auth.getName());
        return ResponseEntity.status(201).body(updated);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId, Authentication auth) {
        service.deleteComment(postId, commentId, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
