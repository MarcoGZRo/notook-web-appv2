package com.notook.platform.handler;

public class InvalidEntityArgumentException extends RuntimeException
{
    public InvalidEntityArgumentException(String description)
    {
        super(description);
    }
}
