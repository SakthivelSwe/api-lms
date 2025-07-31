package com.tvm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exam-service", url = "http://localhost:8085") // adjust port
public interface ExamServiceClient {

    @GetMapping("/exam-result/{studentId}/{courseId}")
    Boolean hasPassedExam(@PathVariable String studentId, @PathVariable String courseId);


}
