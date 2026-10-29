package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.model.enums.GoalReminderFrequency;
import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Nationalized;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GoalReminder
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReminder;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_goal", unique = true)
    private AcademicGoal academicGoal;

    private boolean isEnabled = true;

    @Enumerated(EnumType.STRING)
    @Nationalized
    private GoalReminderFrequency frequencyType;

    private Short intervalDays;

    private LocalDateTime nextReminderAt;

    public GoalReminder(AcademicGoal academicGoal, GoalReminderFrequency frequency,
                        Short intervalDays, LocalDateTime nextReminderAt)
    {
        this.academicGoal = ModelRules.required(academicGoal, "meta academica");
        configure(frequency, intervalDays, nextReminderAt);
    }

    public void configure(GoalReminderFrequency frequency, Short interval, LocalDateTime next)
    {
        frequencyType = ModelRules.required(frequency, "frecuencia");
        ModelRules.require(interval == null || interval > 0, "El intervalo debe ser positivo");
        intervalDays = interval;
        nextReminderAt = next;
    }

    public void enable()
    {
        isEnabled = true;
    }

    public void disable()
    {
        isEnabled = false;
    }
}
