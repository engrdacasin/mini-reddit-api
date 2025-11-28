package com.example.JuniorInterview.Model;


import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    private Integer postId;
    private String title;
    private String content;
    private String owner;
    private Instant createdAt;
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    private String imageName;
    private String imageType;
    private byte[] imageData;
}