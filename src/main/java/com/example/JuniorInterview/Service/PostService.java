package com.example.JuniorInterview.Service;

import com.example.JuniorInterview.Exception.BadRequestException;
import com.example.JuniorInterview.Exception.ForbiddenException;
import com.example.JuniorInterview.Exception.NotFoundException;
import com.example.JuniorInterview.Model.Comment;
import com.example.JuniorInterview.Model.Post;
import com.example.JuniorInterview.Repository.PostRepository;
import com.example.JuniorInterview.dto.CreateCommentRequest;
import com.example.JuniorInterview.dto.CreatePostRequest;
import com.example.JuniorInterview.dto.UpdateCommentRequest;
import com.example.JuniorInterview.dto.UpdatePostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository repo;
    public Post getPost(Integer postId) {
        return repo.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
    }

    public Post createPost(CreatePostRequest request, MultipartFile image, String username) {
        Post p = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .owner(username)
                .createdAt(Instant.now())
                .comments(new ArrayList<>())
                .build();
        if (image != null && !image.isEmpty()) {
            try {
                p.setImageName(image.getOriginalFilename());
                p.setImageType(image.getContentType());
                p.setImageData(image.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read image");
            }
        }

        return repo.save(p);
    }


    public Post updatePost(Integer postId, UpdatePostRequest request, String username) {
        Post existing = getPost(postId);

        if (!existing.getOwner().equals(username)) {
            throw new ForbiddenException("You cannot update someone else's post");
        }
        if ((request.getTitle() == null || request.getTitle().isBlank()) &&
                (request.getContent() == null || request.getContent().isBlank())) {
            throw new BadRequestException("Nothing to update");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            existing.setTitle(request.getTitle());
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            existing.setContent(request.getContent());
        }

        return repo.save(existing);
    }

    public void deletePost(Integer postId, String username) {
        Post existing = getPost(postId);

        if (!existing.getOwner().equals(username)) {
            throw new ForbiddenException("Unable to delete other's posts");
        }

        repo.deleteById(postId);
    }

    public Comment addComment(Integer postId, CreateCommentRequest request, String username) {
        Comment comment = Comment.builder()
                .content(request.getContent())
                .owner(username)
                .createdAt(Instant.now())
                .build();

        return repo.addComment(postId, comment);
    }

    public Comment updateComment(Integer postId, Integer commentId, UpdateCommentRequest request, String username) {
        Post post = getPost(postId);

        Comment comment = post.getComments().stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getOwner().equals(username)) {
            throw new ForbiddenException("You cannot update someone else's comment");
        }
        if ((request.getContent() == null || request.getContent().isBlank())) {
            throw new BadRequestException("Nothing to update");
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            comment.setContent(request.getContent());
        }

        return comment;
    }
    public void deleteComment(Integer postId, Integer commentId, String username) {
        Post post = getPost(postId);

        Comment comment = post.getComments().stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getOwner().equals(username)) {
            throw new ForbiddenException("You cannot delete someone else's comment");
        }

        repo.deleteComment(postId, commentId);
    }

    public List<Post> search(String q, String sortBy, String order, int page, int size) {
        return repo.search(q, sortBy, order, page, size);
    }

    public List<Post> getAllPaginated(String sortBy, String order, int page, int size) {
        List<Post> all = repo.findAll();
        Comparator<Post> comparator;
        switch (sortBy.toLowerCase()) {
            case "title" -> comparator = Comparator.comparing(Post::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "author" -> comparator = Comparator.comparing(Post::getOwner, String.CASE_INSENSITIVE_ORDER);
            default -> comparator = Comparator.comparing(Post::getCreatedAt);
        }
        if (order.equalsIgnoreCase("desc")) comparator = comparator.reversed();
        all.sort(comparator);
        int from = page * size;
        int to = Math.min(from + size, all.size());
        if (from >= all.size()) return List.of();
        return all.subList(from, to);
    }
}