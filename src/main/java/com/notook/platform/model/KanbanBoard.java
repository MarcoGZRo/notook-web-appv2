package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.model.enums.KanbanBoardStates;
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
public class KanbanBoard
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKanbanBoard;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project")
    private Project project;

    @Nationalized
    private String title = "Tablero";

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime savedAt;

    @Enumerated(EnumType.STRING)
    @Nationalized
    private KanbanBoardStates state = KanbanBoardStates.ACTIVO;

    public KanbanBoard(UserAccount userAccount, Project project, String title)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.project = project;
        changeTitle(title == null || title.isBlank() ? "Tablero" : title);
    }

    void requireEditable()
    {
        ModelRules.require(state == KanbanBoardStates.ACTIVO, "Un tablero guardado no puede editarse");
    }

    public void changeTitle(String value)
    {
        requireEditable();
        title = ModelRules.text(value, "titulo del tablero", 80);
    }

    public void saveBoard(LocalDateTime when)
    {
        requireEditable();
        savedAt = ModelRules.required(when, "fecha de guardado");
        state = KanbanBoardStates.GUARDADO;
    }
}
