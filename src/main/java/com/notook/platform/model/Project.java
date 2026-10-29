package com.notook.platform.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import com.notook.platform.utils.Schedule;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.notook.platform.model.enums.ProjectStates;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Project
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_created_by")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Course course;

    private String projectName;

    private String description;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDate startDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ProjectStates state = ProjectStates.ACTIVO;

    private boolean isArchived = false;

    public Project(UserAccount userAccount, Course course, String projectName, String description, LocalDate startDate, LocalDate dueDate)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario creador");
        this.course = course;
        changeProjectName(projectName);
        changeDescription(description);
        reschedule(startDate, dueDate);
    }

    public void changeProjectName(String value)
    {
        projectName = ModelRules.text(value, "nombre del proyecto", 100);
    }

    public void chanceProjectName(String value)
    {
        changeProjectName(value);
    }

    public void changeDescription(String value)
    {
        description = ModelRules.optionalText(value, "descripcion", 500);
    }

    public void changeState(ProjectStates value)
    {
        state = ModelRules.required(value, "estado");
    }

    public void changeState(String value)
    {
        try
        {
            changeState(ProjectStates.valueOf(ModelRules.text(value, "estado", 20)));
        }
        catch (IllegalArgumentException ex)
        {
            throw new com.notook.platform.handler.InvalidEntityArgumentException("Estado del proyecto no valido");
        }
    }

    public void reschedule(LocalDate start, LocalDate due)
    {
        Schedule.validateReschedule(createdAt, start, due);
        startDate = start;
        dueDate = due;
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
