package com.notook.platform.model;

import com.notook.platform.model.id.KanbanBoardTaskId;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class KanbanBoardTask
{
    @EmbeddedId
    private KanbanBoardTaskId id;

    @MapsId("idKanbanBoard")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kanban_board")
    private KanbanBoard kanbanBoard;

    @MapsId("idTask")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_task")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kanban_column")
    private KanbanColumn kanbanColumn;

    private short position = 1;

    public KanbanBoardTask(KanbanBoard board, Task task, KanbanColumn column, short position)
    {
        this.kanbanBoard = ModelRules.required(board, "tablero");
        this.kanbanBoard.requireEditable();
        this.task = ModelRules.required(task, "tarea");
        this.id = new KanbanBoardTaskId();
        moveTo(column, position);
    }

    public void moveTo(KanbanColumn column, short position)
    {
        kanbanBoard.requireEditable();
        ModelRules.required(column, "columna");
        ModelRules.require(column.belongsTo(kanbanBoard), "La columna debe pertenecer al mismo tablero");
        this.kanbanColumn = column;
        this.position = ModelRules.positive(position, "posicion");
    }
}
