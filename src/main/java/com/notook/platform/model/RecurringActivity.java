package com.notook.platform.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class RecurringActivity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActivity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Course course;

    @Nationalized
    private String title;

    private LocalTime startTime;

    private LocalTime endTime;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public RecurringActivity(UserAccount userAccount, Course course, String title, LocalTime start, LocalTime end)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.course = course;
        changeTitle(title);
        changeTime(start, end);
    }

    public void changeTitle(String value)
    {
        title = ModelRules.text(value, "titulo", 80);
    }

    public void changeTime(LocalTime start, LocalTime end)
    {
        ModelRules.required(start, "hora de inicio");
        ModelRules.required(end, "hora de finalizacion");
        ModelRules.require(end.isAfter(start), "La hora final debe ser posterior a la inicial");
        startTime = start;
        endTime = end;
    }
}
