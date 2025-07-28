package com.tvm.dto;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private Double price;
}

