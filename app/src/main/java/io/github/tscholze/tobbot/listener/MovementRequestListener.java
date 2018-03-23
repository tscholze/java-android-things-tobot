package io.github.tscholze.tobbot.listener;

import io.github.tscholze.tobbot.utils.MovementCommand;


/**
 * This listener is responsible for provding a movement related control flows.
 */
public interface MovementRequestListener
{
    /**
     * Indicates that something or someone wants to move the vehicle
     *
     * @param command Requested movement command
     *
     * @return True if movement was correct
     */
    Boolean requestMovement(MovementCommand command);
}
