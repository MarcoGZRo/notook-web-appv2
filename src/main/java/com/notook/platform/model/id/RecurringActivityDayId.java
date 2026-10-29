package com.notook.platform.model.id;

import com.notook.platform.handler.InvalidEmbeddableFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import lombok.Getter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class RecurringActivityDayId implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(name = "id_activity")
    private Long idActivity;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "day_of_week", columnDefinition = "tinyint")
    private short dayOfWeek;

    public RecurringActivityDayId(Long idActivity, short dayOfWeek)
    {
        if (idActivity == null || idActivity <= 0) throw new InvalidEmbeddableFieldException("El id de la actividad debe ser positivo");
        validateDay(dayOfWeek);
        this.idActivity = idActivity;
        this.dayOfWeek = dayOfWeek;
    }

    public static void validateDay(short day)
    {
        if (day < 1 || day > 7) throw new InvalidEmbeddableFieldException("El dia debe estar entre 1 (lunes) y 7 (domingo)");
    }

    public void assignDay(short day)
    {
        validateDay(day); dayOfWeek = day;
    }
}
