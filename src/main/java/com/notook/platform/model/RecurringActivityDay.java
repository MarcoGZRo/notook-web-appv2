package com.notook.platform.model;

import com.notook.platform.model.id.RecurringActivityDayId;
import com.notook.platform.utils.ModelRules;

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
public class RecurringActivityDay
{
    @EmbeddedId
    private RecurringActivityDayId id;

    @MapsId("idActivity")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_activity")
    private RecurringActivity recurringActivity;

    public RecurringActivityDay(RecurringActivity recurringActivity, short dayOfWeek)
    {
        this.recurringActivity = ModelRules.required(recurringActivity, "actividad");
        RecurringActivityDayId.validateDay(dayOfWeek);
        this.id = new RecurringActivityDayId();
        this.id.assignDay(dayOfWeek);
    }
}
