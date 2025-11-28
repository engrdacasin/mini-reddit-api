package com.example.JuniorInterview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {
    @NotBlank
    private String content;
}