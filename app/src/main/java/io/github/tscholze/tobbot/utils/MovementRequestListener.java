package io.github.tscholze.tobbot.utils;

/**
 * Created by tscholze on 19.03.18.
 */

public interface MovementRequestListener
{
    /**
     * Indicates that something or someone wants to move the vehicle
     *
     * @param command Requested movement command
     * @return True if movement was correct
     */
    Boolean requestMovement(MovementCommand command);
}
