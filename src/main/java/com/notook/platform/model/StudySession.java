package com.notook.platform.model;

import com.notook.platform.model.enums.StudySessionMode;
import com.notook.platform.model.enums.StudySessionStates;
import com.notook.platform.utils.ModelRules;
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
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_course") private Course course;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_task") private Task task;

    @Enumerated(EnumType.STRING)
    private StudySessionMode mode;

    private short focusDurationMinutes;

    private short breakDurationMinutes;

    private short plannedCycles;

    private short completedCycles;

    private int effectiveStudySeconds;

    @Generated(event = EventType.INSERT)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    private StudySessionStates state = StudySessionStates.ACTIVA;

    public StudySession(UserAccount user, Course course, Task task, StudySessionMode mode, short focusMinutes, short breakMinutes, short plannedCycles)
    {
        userAccount = ModelRules.required(user, "usuario");
        this.course = course;
        this.task = task;
        this.mode = ModelRules.required(mode, "modo");
        configure(focusMinutes, breakMinutes, plannedCycles);
    }

    public void configure(short focus, short rest, short cycles)
    {
        ModelRules.require(state == StudySessionStates.ACTIVA, "Solo se puede configurar una sesion activa");
        focusDurationMinutes = ModelRules.positive(focus, "minutos de concentracion");
        breakDurationMinutes = ModelRules.positive(rest, "minutos de descanso");
        ModelRules.require(cycles > 0 && cycles <= 255, "Los ciclos previstos deben estar entre 1 y 255");
        ModelRules.require(cycles >= completedCycles, "No se puede programar menos ciclos que los completados");
        plannedCycles = cycles;
    }
    public void recordProgress(short cycles, int effectiveSeconds)
    {
        ModelRules.require(state != StudySessionStates.FINALIZADA && state != StudySessionStates.CANCELADA,
                "La sesion esta cerrada");
        ModelRules.require(cycles >= 0 && cycles <= plannedCycles, "Los ciclos completados son invalidos");
        completedCycles = cycles;
        effectiveStudySeconds = ModelRules.nonNegative(effectiveSeconds, "segundos efectivos");
    }

    public void pause()
    {
        ModelRules.require(state == StudySessionStates.ACTIVA, "Solo una sesion activa puede pausarse");
        state = StudySessionStates.PAUSADA;
    }

    public void resume()
    {
        ModelRules.require(state == StudySessionStates.PAUSADA, "Solo una sesion pausada puede reanudarse");
        state = StudySessionStates.ACTIVA;
    }

    public void finish(LocalDateTime at)
    {
        close(at, StudySessionStates.FINALIZADA);
    }

    public void cancel(LocalDateTime at)
    {
        close(at, StudySessionStates.CANCELADA);
    }

    private void close(LocalDateTime at, StudySessionStates next)
    {
        ModelRules.require(state == StudySessionStates.ACTIVA || state == StudySessionStates.PAUSADA, "La sesion ya esta cerrada");
        ModelRules.required(at, "fecha de cierre");
        ModelRules.require(startedAt == null || !at.isBefore(startedAt), "El cierre no puede preceder al inicio");
        finishedAt = at;
        state = next;
    }
}
