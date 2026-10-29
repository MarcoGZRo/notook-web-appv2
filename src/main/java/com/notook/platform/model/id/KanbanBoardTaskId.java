package com.notook.platform.model.id;

import com.notook.platform.handler.InvalidEmbeddableFieldException;
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
public class KanbanBoardTaskId implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "id_kanban_board")
    private Long idKanbanBoard;

    @Column(name = "id_task")
    private Long idTask;

    public KanbanBoardTaskId(Long idKanbanBoard, Long idTask)
    {
        if (idKanbanBoard == null || idKanbanBoard <= 0 || idTask == null || idTask <= 0) throw new InvalidEmbeddableFieldException("Los ids del tablero y la tarea deben ser positivos");
        this.idKanbanBoard = idKanbanBoard;
        this.idTask = idTask;
    }
}
