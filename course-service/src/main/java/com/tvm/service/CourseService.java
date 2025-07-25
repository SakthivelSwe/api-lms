package com.tvm.service;

import com.tvm.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(CourseDTO courseDTO);

    List<CourseDTO> getAllCourses();

    CourseDTO updateCourse(Long courseId, CourseDTO courseDTO);

    void deleteCourse(Long courseId);
}
