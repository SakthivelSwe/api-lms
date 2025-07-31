package com.tvm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "auth-service", url = "http://localhost:8081")
public interface AuthClient {

    @GetMapping("/auth/validate")
    ResponseEntity<Map<String, String>> validateToken(@RequestHeader("Authorization") String tokenHeader);
}