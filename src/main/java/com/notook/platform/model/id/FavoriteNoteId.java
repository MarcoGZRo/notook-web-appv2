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
public class FavoriteNoteId implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "id_note")
    private Long idNote;

    public FavoriteNoteId(Long user, Long note) {
        if (user == null || user <= 0 || note == null || note <= 0)
            throw new InvalidEmbeddableFieldException("Los ids del usuario y la nota deben ser positivos");
        idUser = user;
        idNote = note;
    }
}
