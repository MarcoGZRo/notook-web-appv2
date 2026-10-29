package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Nationalized;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Note
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course")
    private Course course;

    @Nationalized
    private String title;

    // SQL Server usa NVARCHAR(MAX) para esta columna.
    @JdbcTypeCode(SqlTypes.LONG32NVARCHAR)
    private String content;

    @Generated(event = EventType.INSERT)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isArchived = false;

    private boolean isPublic = false;

    public Note(UserAccount userAccount, Course course, String title, String content)
    {
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.course = course;
        changeTitle(title);
        changeContent(content);
    }

    public void changeTitle(String value)
    {
        title = ModelRules.text(value, "titulo", 80);
    }

    public void changeContent(String value)
    {
        ModelRules.require(value != null && !value.isBlank(), "El contenido es obligatorio");
        content = value;
    }

    @PrePersist
    @PreUpdate
    private void updateTimestamp()
    {
        updatedAt = LocalDateTime.now();
    }

    public void archive()
    {
        isArchived = true;
    }

    public void restore()
    {
        isArchived = false;
    }

    public void makePublic()
    {
        isPublic = true;
    }

    public void makePrivate()
    {
        isPublic = false;
    }
}
