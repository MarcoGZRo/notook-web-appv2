
package com.notook.platform.model;

import com.notook.platform.utils.ModelRules;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String userName;

    private String email;

    private @Getter(AccessLevel.NONE) String passwordHash;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private boolean isActive = true;

    public void setUserName(String userName)
    {
        String value = ModelRules.text(userName, "nombre de usuario", 80);
        ModelRules.require(value.length() >= 5, "El nombre debe tener al menos 5 caracteres");
        ModelRules.require(value.matches("^[A-Za-z0-9].*"), "El nombre debe comenzar con una letra o numero");
        this.userName = value;
    }

    public void setEmail(String email)
    {
        String value = ModelRules.text(email, "correo", 100);
        ModelRules.require(value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"), "El formato del correo no es valido");
        this.email = value;
    }

    public void changePasswordHash(String passwordHash)
    {
        this.passwordHash = ModelRules.text(passwordHash, "hash de contrasena", 255);
    }

    public void activate()
    {
        this.isActive = true;
    }

    public void deactivate()
    {
        this.isActive = false;
    }
}
