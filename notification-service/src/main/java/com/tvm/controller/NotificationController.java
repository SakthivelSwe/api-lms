package com.tvm.controller;

import com.tvm.dto.NotificationRequestDTO;
import com.tvm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public String sendMail(@RequestBody NotificationRequestDTO requestDTO) {
        notificationService.sendNotification(requestDTO);
        return "Notification sent successfully!";
    }

    @GetMapping("/test")
    public String sendTestMail(@RequestParam String to) {
        notificationService.sendTestEmail(to);
        return "Test mail sent to " + to;
    }
}