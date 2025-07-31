package com.tvm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8082") // adjust port
public interface UserServiceClient {

    //fetch enrollment status
    @GetMapping("/api/enrollments/status")
    String getEnrollmentStatus(@RequestParam String studentId,
                               @RequestParam String courseId);

    //fetch student name from user service
    @GetMapping("/student-name/{studentId}")
    String getStudentName(@PathVariable String studentId);

    //fetch student email
    @GetMapping("/email-name/{studentId}")
    String getEmailName(@PathVariable String studentId);

}

