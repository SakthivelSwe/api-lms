package com.tvm.service.impl;

import com.tvm.dto.CourseDTO;
import com.tvm.exception.ResourceNotFoundException;
import com.tvm.exception.UnauthorizedException;
import com.tvm.model.CourseEntity;
import com.tvm.model.ModuleEntity;
import com.tvm.repository.CourseRepository;
import com.tvm.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public CourseDTO createCourse(CourseDTO dto) {
        CourseEntity course = new CourseEntity();
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setDuration(dto.getDuration());
        course.setPrice(dto.getPrice());

        CourseEntity saved = courseRepository.save(course);
        return new CourseDTO(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getDuration(), saved.getPrice());
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> new CourseDTO(course.getId(), course.getTitle(), course.getDescription(), course.getDuration(), course.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        CourseEntity course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id",  id));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setDuration(dto.getDuration());
        course.setPrice(dto.getPrice());

        CourseEntity updated = courseRepository.save(course);
        return new CourseDTO(updated.getId(), updated.getTitle(), updated.getDescription(), updated.getDuration(), updated.getPrice());
    }
}

