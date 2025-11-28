package com.example.JuniorInterview.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentRequest {
    private String content;
}