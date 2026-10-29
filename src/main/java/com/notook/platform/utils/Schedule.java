package com.notook.platform.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Schedule
{
    public static void validateReschedule(LocalDateTime createdAt, LocalDate startDate, LocalDate endDate) {
        ModelRules.require(startDate == null || endDate == null || !endDate.isBefore(startDate),
                "La fecha de finalizacion no puede ser anterior a la de inicio");
    }
}
