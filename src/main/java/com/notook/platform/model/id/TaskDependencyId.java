package com.notook.platform.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class TaskDependencyId implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "id_task")
    private Long task;

    @Column(name = "id_depends_on_task")
    private Long dependsOnTask;

    public TaskDependencyId(Long task, Long dependsOnTask)
    {
        if (task == null || task <= 0 || dependsOnTask == null || dependsOnTask <= 0 || task.equals(dependsOnTask))
            throw new com.notook.platform.handler.InvalidEmbeddableFieldException("Los ids de dependencia deben ser positivos y diferentes");
        this.task = task;
        this.dependsOnTask = dependsOnTask;
    }
}
