package com.notook.platform.model;

import com.notook.platform.model.enums.ProjectRole;
import com.notook.platform.utils.ModelRules;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Role
{
    @Id
    @JdbcTypeCode(SqlTypes.TINYINT) @Column(name = "id_role", columnDefinition = "tinyint")
    private short id;

    @Enumerated(value = EnumType.STRING)
    private ProjectRole roleName;

    public Role(short id, ProjectRole roleName) {
        ModelRules.require(id > 0 && id <= 255, "El identificador del rol debe estar entre 1 y 255");
        this.id = id;
        this.roleName = ModelRules.required(roleName, "nombre del rol");
    }
}
