package com.notook.platform.utils;

import com.notook.platform.handler.InvalidEntityArgumentException;
import java.math.BigDecimal;

public final class ModelRules {
    private ModelRules() {}

    public static void require(boolean condition, String message)
    {
        if (!condition) throw new InvalidEntityArgumentException(message);
    }

    public static <T> T required(T value, String name)
    {
        require(value != null, "El campo " + name + " es obligatorio");
        return value;
    }

    public static String text(String value, String name, int max)
    {
        require(value != null && !value.isBlank(), "El campo " + name + " es obligatorio");
        String clean = value.trim();
        require(clean.length() <= max, "El campo " + name + " no puede superar " + max + " caracteres");
        return clean;
    }

    public static String optionalText(String value, String name, int max)
    {
        if (value == null || value.isBlank()) return null;
        String clean = value.trim();
        require(clean.length() <= max, "El campo " + name + " no puede superar " + max + " caracteres");
        return clean;
    }

    public static short positive(short value, String name)
    {
        require(value > 0, "El campo " + name + " debe ser mayor que cero");
        return value;
    }

    public static int nonNegative(int value, String name)
    {
        require(value >= 0, "El campo " + name + " no puede ser negativo");
        return value;
    }

    public static BigDecimal amount(BigDecimal value, String name, boolean allowZero)
    {
        required(value, name);
        require(value.signum() > 0 || (allowZero && value.signum() == 0),
                "El campo " + name + (allowZero ? " no puede ser negativo" : " debe ser mayor que cero"));
        require(value.precision() - value.scale() <= 10 && value.scale() <= 2,
                "El campo " + name + " debe ajustarse a DECIMAL(12,2)");
        return value;
    }
}
