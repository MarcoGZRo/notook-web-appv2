package com.notook.platform.model;

import com.notook.platform.model.enums.NotificationType;
import com.notook.platform.utils.ModelRules;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String title;

    private String message;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private boolean isRead;

    private LocalDateTime readAt;

    public Notification(UserAccount user, NotificationType type, String title, String message)
    {
        userAccount = ModelRules.required(user, "usuario");
        notificationType = ModelRules.required(type, "tipo de notificacion");
        this.title = ModelRules.text(title, "titulo", 100);
        this.message = ModelRules.text(message, "mensaje", 500);
    }

    public void markRead(LocalDateTime at)
    {
        readAt = ModelRules.required(at, "fecha de lectura");
        isRead = true;
    }
    public void markUnread()
    {
        readAt = null;
        isRead = false;
    }
}
