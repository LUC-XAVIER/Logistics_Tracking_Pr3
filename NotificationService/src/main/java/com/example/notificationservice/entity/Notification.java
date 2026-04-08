package com.example.notificationservice.entity;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationEventType;
import com.example.notificationservice.enums.NotificationStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private UUID id;

    private UUID userId;       // added — owner of this notification

    private UUID parcelId;     // nullable for payment-only notifications

    private String title;      // added — shown in bell dropdown header

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String recipientEmail;

    private String recipientPhone;

    @Lob
    private String message;

    @Builder.Default
    private boolean read = false;  // added — for bell icon unread state

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        if (status == null) status = NotificationStatus.PENDING;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}