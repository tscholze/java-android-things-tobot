package io.github.tscholze.tobbot.utils;

/**
 * Represents and converts all available TobboT commands
 * between raw representation and state value.

 * The source is heavily inspired by:
 * https://github.com/androidthings/robocar/blob/launch/shared/src/main/java/com/example/androidthings/robocar/shared/CarCommands.java
 */
public class VehicleCommand
{
    /**
     * Command for moving forward (raw value: 0)
     */
    public static final byte MOVE_FORWARD = 0;

    /**
     * Command for moving left (raw value: 1)
     */
    public static final byte MOVE_LEFT = 1;

    /**
     * Command for moving right (raw value: 2)
     */
    public static final byte MOVE_RIGHT = 2;

    /**
     * Command for moving backwards (raw value: 3)
     */
    public static final byte MOVE_BACKWARDS = 3;

    /**
     * Command to stop the movement (raw value: 4)
     */
    public static final byte STOP = 4;

    /**
     * Indicates an error
     */
    public static final byte ERROR = -1;
}
