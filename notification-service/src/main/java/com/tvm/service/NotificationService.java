package com.tvm.service;

import com.tvm.dto.NotificationRequestDTO;

public interface NotificationService {
    void sendNotification(NotificationRequestDTO requestDTO);

    void sendTestEmail(String to);
}