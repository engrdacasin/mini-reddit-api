package com.example.JuniorInterview.Repository;

import com.example.JuniorInterview.Model.Comment;
import com.example.JuniorInterview.Model.Post;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryRepository implements  PostRepository{

    private final Map<Integer, Post> store = new ConcurrentHashMap<>();
    private final AtomicInteger postIdGen = new AtomicInteger(1);
    private final AtomicInteger commentIdGen = new AtomicInteger(1);

    @Override
    public List<Post> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return Optional.ofNullable(store.get(postId));
    }

    @Override
    public Post save(Post post) {
        if (post.getPostId() == null) {
            post.setPostId(postIdGen.getAndIncrement());
            post.setCreatedAt(Instant.now());
        }

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }

        store.put(post.getPostId(), post);
        return post;
    }

    @Override
    public void deleteById(Integer postId) {
        store.remove(postId);
    }

    @Override
    public Comment addComment(Integer postId, Comment comment) {
        Post post = store.get(postId);
        if (post == null) throw new NoSuchElementException("Post not found");

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }

        if (comment.getCommentId() == null) {
            comment.setCommentId(commentIdGen.getAndIncrement());
            comment.setCreatedAt(Instant.now());
        }

        post.getComments().add(comment);
        return comment;
    }

    @Override
    public void deleteComment(Integer postId, Integer commentId) {
        Post post = store.get(postId);
        if (post == null) throw new NoSuchElementException("Post not found");

        post.getComments().removeIf(c -> c.getCommentId() == commentId);
    }
    @Override
    public List<Post> search(String q, String sortBy, String order, int page, int size) {
        String query = q.toLowerCase();

        String[] keywords = query.split("\\s+");

        List<Post> filtered = store.values().stream()
                .filter(post -> {
                    String title = Optional.ofNullable(post.getTitle()).orElse("").toLowerCase();
                    String content = Optional.ofNullable(post.getContent()).orElse("").toLowerCase();
                    String author = Optional.ofNullable(post.getOwner()).orElse("").toLowerCase();

                    return Arrays.stream(keywords).allMatch(kw ->
                            title.contains(kw) ||
                                    content.contains(kw) ||
                                    author.contains(kw)
                    );
                })
                .collect(Collectors.toList());

        Comparator<Post> comparator;

        switch (sortBy.toLowerCase()) {
            case "title" -> comparator = Comparator.comparing(Post::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "author" -> comparator = Comparator.comparing(Post::getOwner, String.CASE_INSENSITIVE_ORDER);
            default -> comparator = Comparator.comparing(Post::getCreatedAt);
        }

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);

        int from = page * size;
        int to = Math.min(from + size, filtered.size());

        if (from >= filtered.size()) return List.of();

        return filtered.subList(from, to);
    }
}