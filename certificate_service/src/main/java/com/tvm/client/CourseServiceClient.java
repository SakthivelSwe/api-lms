package com.tvm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", url = "http://localhost:8083")
public interface CourseServiceClient {

    //fetch course name from course service
    @GetMapping("/course-name/{courseId}")
    String getCourseName(@PathVariable String courseId);
}


