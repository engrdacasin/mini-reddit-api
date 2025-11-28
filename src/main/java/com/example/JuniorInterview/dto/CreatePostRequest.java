package com.example.JuniorInterview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
