package io.github.tscholze.tobbot.utils;

public enum MovementCommand
{
    FORWARD, RIGHT, BACKWARD, LEFT, STOP, ERROR, NOT_FOUND;

    public static MovementCommand fromString(String string)
    {
        try
        {
            return MovementCommand.valueOf(string.toUpperCase());
        }
        catch (Exception e)
        {
            System.out.println(e.getLocalizedMessage());
            return MovementCommand.NOT_FOUND;
        }
    }
}
