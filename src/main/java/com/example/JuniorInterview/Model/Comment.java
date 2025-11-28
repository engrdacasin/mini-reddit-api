package com.example.JuniorInterview.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private Integer commentId;
    private String content;
    private String owner;
    private Instant createdAt;

}
