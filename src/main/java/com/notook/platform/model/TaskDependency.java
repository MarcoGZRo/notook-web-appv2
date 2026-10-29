package com.notook.platform.model;

import com.notook.platform.model.id.TaskDependencyId;
import com.notook.platform.utils.ModelRules;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskDependency {
    @EmbeddedId
    private TaskDependencyId id;

    @MapsId("task")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_task")
    private Task task;

    @MapsId("dependsOnTask")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_depends_on_task")
    private Task dependsOnTask;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public TaskDependency(Task task, Task dependsOnTask)
    {
        this.task = ModelRules.required(task, "tarea");
        this.dependsOnTask = ModelRules.required(dependsOnTask, "tarea de dependencia");
        ModelRules.require(!task.isSameTask(dependsOnTask), "Una tarea no puede depender de si misma");
        this.id = new TaskDependencyId();
    }
}
