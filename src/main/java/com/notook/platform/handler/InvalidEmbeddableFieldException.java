package com.notook.platform.handler;

public class InvalidEmbeddableFieldException extends RuntimeException
{
    public InvalidEmbeddableFieldException(String description)
    {
        super(description);
    }
}
