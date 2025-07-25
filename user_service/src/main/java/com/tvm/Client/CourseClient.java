package com.tvm.Client;

import com.tvm.DTO.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "course-client", url = """
        http://localhost:8083""")
public interface CourseClient {

    // GET all courses
    @GetMapping("/course/all")
    List<CourseDTO> getAllCourses();

    // GET course by ID
    @GetMapping("/course/getCourse/{id}")
    CourseDTO getCourseById(@PathVariable("id") String courseId);
}