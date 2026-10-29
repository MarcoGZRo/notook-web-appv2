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
public class ProjectMemberId implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "id_project")
    private Long idProject;

    @Column(name = "id_user")
    private Long idUser;

    public ProjectMemberId(Long idProject, Long idUser)
    {
        setIdProject(idProject);
        setIdUser(idUser);
    }

    public void setIdProject(Long id)
    {
        if (id == null || id <= 0) throw new InvalidEmbeddableFieldException("El id del proyecto debe ser positivo");
        idProject = id;
    }

    public void setIdUser(Long id)
    {
        if (id == null || id <= 0) throw new InvalidEmbeddableFieldException("El id del usuario debe ser positivo");
        idUser = id;
    }

    public void serIdUser(Long id)
    {
        setIdUser(id);
    }
}
