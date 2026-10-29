
package com.notook.platform.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import com.notook.platform.utils.Schedule;
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

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Course
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCourse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserAccount userAccount;

    private String courseName;

    private String description;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isArchived = false;

    public Course(UserAccount userAccount, String courseName, String description, LocalDate startDate, LocalDate endDate)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        setCourseName(courseName);
        setDescription(description);
        reschedule(startDate, endDate);
    }

    public void setCourseName(String value)
    {
        courseName = ModelRules.text(value, "nombre del curso", 80);
    }

    public void setDescription(String value)
    {
        description = ModelRules.optionalText(value, "descripcion", 255);
    }

    public void reschedule(LocalDate start, LocalDate end)
    {
        Schedule.validateReschedule(createdAt, start, end);
        startDate = start;
        endDate = end;
    }

    public void archive()
    {
        isArchived = true;
    }

    public void restore()
    {
        isArchived = false;
    }
}