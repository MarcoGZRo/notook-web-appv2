package com.notook.platform.model;

import java.time.LocalDateTime;

import com.notook.platform.utils.ModelRules;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.notook.platform.model.id.ProjectMemberId;

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
public class ProjectMember
{
    @EmbeddedId
    private ProjectMemberId projectMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProject")
    @JoinColumn(name = "id_project")
    private Project project;

    @MapsId("idUser")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    private Role role;

    @Generated(event = EventType.INSERT)
    private LocalDateTime joinedAt;

    private boolean isActive = true;

    public ProjectMember(Project project, UserAccount userAccount, Role role)
    {
        this.project = ModelRules.required(project, "proyecto");
        this.userAccount = ModelRules.required(userAccount, "usuario");
        this.projectMemberId = new ProjectMemberId();
        changeRole(role);
    }

    public void changeRole(Role role)
    {
        this.role = ModelRules.required(role, "rol");
    }

    public void active()
    {
        isActive = true;
    }

    public void deactivate()
    {
        isActive = false;
    }
}
