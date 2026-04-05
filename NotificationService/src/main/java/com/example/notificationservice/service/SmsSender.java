package com.example.notificationservice.service;

public interface SmsSender {

    void send(String recipientPhone, String message);
}
