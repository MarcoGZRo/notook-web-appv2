package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Nationalized;
import org.hibernate.generator.EventType;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CalendarEvent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Course course;

    @Nationalized
    private String title;

    @Nationalized
    private String description;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public CalendarEvent(UserAccount userAccount, Course course, String title,
                         String description, LocalDateTime startAt, LocalDateTime endAt)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.course = course;
        changeTitle(title);
        changeDescription(description);
        reschedule(startAt, endAt);
    }

    public void changeTitle(String value)
    {
        title = ModelRules.text(value, "titulo", 80);
    }

    public void changeDescription(String value)
    {
        description = ModelRules.optionalText(value, "descripcion", 255);
    }

    public void reschedule(LocalDateTime start, LocalDateTime end)
    {
        ModelRules.required(start, "fecha de inicio");
        ModelRules.require(end == null || end.isAfter(start), "La fecha final debe ser posterior a la inicial");
        startAt = start;
        endAt = end;
    }
}
