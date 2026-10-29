package com.notook.platform.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.notook.platform.model.enums.AcademicGoalStates;
import com.notook.platform.model.enums.GoalMeasurementType;
import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Nationalized;
import org.hibernate.generator.EventType;

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
public class AcademicGoal
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGoal;

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

    @Enumerated(EnumType.STRING)
    @Nationalized
    private GoalMeasurementType measurementType;

    private BigDecimal targetValue;

    private BigDecimal currentValue = BigDecimal.ZERO;

    private LocalDate startDate;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Nationalized
    private AcademicGoalStates state = AcademicGoalStates.ACTIVO;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime lastProgressAt;

    private LocalDateTime achievedAt;

    public AcademicGoal(UserAccount userAccount, Course course, String title, String description,
                        GoalMeasurementType measurementType, BigDecimal targetValue,
                        LocalDate startDate, LocalDate deadline)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.course = course;
        changeTitle(title);
        changeDescription(description);
        this.measurementType = ModelRules.required(measurementType, "tipo de medicion");
        changeTarget(targetValue);
        reschedule(startDate, deadline);
    }

    public void changeTitle(String value)
    {
        title = ModelRules.text(value, "titulo", 100);
    }

    public void changeDescription(String value)
    {
        description = ModelRules.text(value, "descripcion", 500);
    }

    public void changeTarget(BigDecimal value)
    {
        ModelRules.require(state == AcademicGoalStates.ACTIVO, "La meta no esta activa");
        targetValue = ModelRules.amount(value, "valor objetivo", false);
    }

    public void reschedule(LocalDate start, LocalDate deadline)
    {
        ModelRules.required(start, "fecha inicial");
        ModelRules.required(deadline, "fecha limite");
        ModelRules.require(!deadline.isBefore(start), "La fecha limite no puede preceder a la inicial");
        startDate = start;
        this.deadline = deadline;
    }

    public void recordProgress(BigDecimal value, LocalDateTime at)
    {
        ModelRules.require(state == AcademicGoalStates.ACTIVO, "La meta no esta activa");
        currentValue = ModelRules.amount(value, "valor actual", true);
        lastProgressAt = ModelRules.required(at, "fecha de progreso");

        if (currentValue.compareTo(targetValue) >= 0)
        {
            state = AcademicGoalStates.ALCANZADO;
            achievedAt = at;
        }
    }

    public void cancel()
    {
        ModelRules.require(state == AcademicGoalStates.ACTIVO, "La meta no esta activa");
        state = AcademicGoalStates.CANCELADO;
    }
}
