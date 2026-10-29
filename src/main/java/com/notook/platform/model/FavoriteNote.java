package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.model.id.FavoriteNoteId;
import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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
public class FavoriteNote
{
    @EmbeddedId
    private FavoriteNoteId id;

    @MapsId("idUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @MapsId("idNote")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_note")
    private Note note;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    public FavoriteNote(UserAccount userAccount, Note note)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.note = ModelRules.required(note, "nota");
        this.id = new FavoriteNoteId();
    }
}
