package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Nationalized;
import org.hibernate.generator.EventType;

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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class KanbanColumn
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKanbanColumn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kanban_board")
    private KanbanBoard kanbanBoard;

    @Nationalized
    private String name;

    private short position;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public KanbanColumn(KanbanBoard kanbanBoard, String name, short position)
    {
        this.kanbanBoard = ModelRules.required(kanbanBoard, "tablero");
        this.kanbanBoard.requireEditable();
        changeName(name);
        changePosition(position);
    }

    boolean belongsTo(KanbanBoard board)
    {
        if (kanbanBoard == board)
        {
            return true;
        }

        return board != null && kanbanBoard != null
                && kanbanBoard.getIdKanbanBoard() != null
                && kanbanBoard.getIdKanbanBoard().equals(board.getIdKanbanBoard());
    }

    public void changeName(String value)
    {
        kanbanBoard.requireEditable();
        name = ModelRules.text(value, "nombre de la columna", 40);
    }

    public void changePosition(short value)
    {
        kanbanBoard.requireEditable();
        position = ModelRules.positive(value, "posicion");
    }
}
