package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.handler.InvalidEntityArgumentException;
import com.notook.platform.model.enums.TaskPriority;
import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.notook.platform.model.enums.TaskStates;

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
public class Task
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assigned_user")
    private Deliverable deliverable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_deliverable")
    private UserAccount assignedUser;

    private String taskName;

    private String description;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime dueAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Enumerated(value = EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(value = EnumType.STRING)
    private TaskStates state = TaskStates.NO_INICIADO;

    private boolean isArchived = false;

    private boolean isBegun = false;

    public Task(UserAccount userAccount, Course course, Project project, Deliverable deliverable, UserAccount assignedUser, String taskName, String description, LocalDateTime dueAt,
                LocalDateTime startedAt, LocalDateTime completedAt, String priority)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario creador");
        this.course = course;
        changeProjectAndDeliverable(project, deliverable);
        this.assignedUser = assignedUser;
        changeTaskName(taskName);
        changeDescription(description);
        changePriority(priority);
        reschedule(dueAt, startedAt, completedAt);

        if (completedAt != null)
        {
            ModelRules.require(startedAt != null, "Una tarea completada requiere fecha de inicio");
            state = TaskStates.FINALIZADA;
        } else if (startedAt != null)
        {
            state = TaskStates.EN_PROGRESO;
        }
    }

    public void changeProjectAndDeliverable(Project project, Deliverable deliverable)
    {
        requireEditable();
        ModelRules.require(deliverable == null || project != null, "Un entregable requiere un proyecto");
        this.project = project;
        this.deliverable = deliverable;
    }

    public void changeTaskName(String value)
    {
        requireEditable();
        taskName = ModelRules.text(value, "nombre de la tarea", 80);
    }

    public void changeDescription(String value)
    {
        requireEditable();
        description = ModelRules.optionalText(value, "descripcion", 255);
    }

    public void reschedule(LocalDateTime due, LocalDateTime started, LocalDateTime completed)
    {
        requireEditable();
        ModelRules.require(started == null || completed == null || !completed.isBefore(started),
                "La fecha de finalizacion no puede ser anterior al comienzo");

        if (state == TaskStates.FINALIZADA && completed == null)
        {
            throw new InvalidEntityArgumentException("Una tarea finalizada requiere fecha de finalizacion");
        }

        dueAt = due;
        startedAt = started;
        completedAt = completed;
        isBegun = started != null;
    }

    public void changeDueAt(LocalDateTime value)
    {
        reschedule(value, startedAt, completedAt);
    }

    public void changeStartedAt(LocalDateTime value)
    {
        ModelRules.require(!isBegun, "La tarea ya comenzo");
        reschedule(dueAt, value, completedAt);
    }

    public void changeCompletedAt(LocalDateTime value)
    {
        reschedule(dueAt, startedAt, value);
    }

    public void changePriority(TaskPriority value)
    {
        requireEditable();
        priority = value == null ? TaskPriority.NINGUNO : value;
    }

    public void changePriority(String value)
    {
        if (value == null || value.isBlank())
        {
            changePriority(TaskPriority.NINGUNO);
            return;
        }

        try
        {
            changePriority(TaskPriority.valueOf(value.trim()));
        }

        catch (IllegalArgumentException ex)
        {
            throw new InvalidEntityArgumentException("Prioridad de la tarea no valida");
        }
    }

    public void changeState(TaskStates next)
    {
        ModelRules.required(next, "estado");
        requireEditable();
        if (state == next) return;
        boolean allowed = switch (state)
        {
            case NO_INICIADO -> next == TaskStates.EN_PROGRESO;
            case EN_PROGRESO -> next == TaskStates.EN_PAUSA
                    || next == TaskStates.BLOQUEADA || next == TaskStates.FINALIZADA;
            case EN_PAUSA, BLOQUEADA -> next == TaskStates.EN_PROGRESO;
            case FINALIZADA -> false;
        };

        ModelRules.require(allowed, "Transicion de estado de tarea no permitida");
        if (next == TaskStates.EN_PROGRESO)
        {
            ModelRules.require(startedAt != null, "La tarea debe tener fecha de inicio");
            isBegun = true;
        }

        if (next == TaskStates.FINALIZADA)
        {
            ModelRules.require(completedAt != null, "La tarea debe tener fecha de finalizacion");
        }

        state = next;
    }

    public void begin(LocalDateTime at)
    {
        ModelRules.require(state == TaskStates.NO_INICIADO, "Solo se pueden iniciar tareas no iniciadas");
        changeStartedAt(ModelRules.required(at, "fecha de inicio"));
        changeState(TaskStates.EN_PROGRESO);
    }

    public void pause()
    {
        changeState(TaskStates.EN_PAUSA);
    }

    public void block()
    {
        changeState(TaskStates.BLOQUEADA);
    }

    public void resume()
    {
        changeState(TaskStates.EN_PROGRESO);
    }

    public void finish(LocalDateTime at)
    {
        ModelRules.require(state == TaskStates.EN_PROGRESO, "La tarea debe estar en progreso");
        changeCompletedAt(ModelRules.required(at, "fecha de finalizacion"));
        changeState(TaskStates.FINALIZADA);
    }

    private void requireEditable()
    {
        ModelRules.require(state != TaskStates.FINALIZADA, "Una tarea finalizada no puede modificarse");
    }

    boolean isSameTask(Task other)
    {
        return this == other || (other != null && idTask != null && idTask.equals(other.idTask));
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
