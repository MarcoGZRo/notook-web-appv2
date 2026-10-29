package com.notook.platform.model;

import java.time.LocalDateTime;


import com.notook.platform.handler.InvalidEntityArgumentException;
import com.notook.platform.utils.ModelRules;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.notook.platform.model.enums.DeliverableStates;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Deliverable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDeliverable;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    private String title;

    private String description;

    private LocalDateTime dueAt;

    @Enumerated(value = EnumType.STRING)
    private DeliverableStates state = DeliverableStates.PENDIENTE;

    private short position;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public Deliverable(Project project, String title, String description, LocalDateTime dueAt, short position)
    {
        this.project = ModelRules.required(project, "proyecto");
        changeTitle(title);
        changeDescription(description);
        changeDueAt(dueAt);
        changePosition(position);
    }

    public void changeTitle(String value)
    {
        title = ModelRules.text(value, "titulo", 100);
    }

    public void changeDescription(String value)
    {
        description = ModelRules.optionalText(value, "descripcion", 500);
    }

    public void changeDueAt(LocalDateTime value)
    {
        dueAt = value;
    }

    public void changePosition(short value)
    {
        position = ModelRules.positive(value, "posicion");
    }

    public void changeDeliverableState(DeliverableStates value)
    {
        state = ModelRules.required(value, "estado");
    }

    public void changeDeliverableState(String value)
    {
        try
        {
            changeDeliverableState(DeliverableStates.valueOf(ModelRules.text(value, "estado", 20)));
        }
        catch (IllegalArgumentException ex)
        {
            throw new InvalidEntityArgumentException("Estado del entregable no valido");
        }
    }
}
