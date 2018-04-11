package io.github.tscholze.tobbot.models;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

import io.github.tscholze.tobbot.utils.MovementCommand;

@IgnoreExtraProperties
public class RemoteCommand extends BaseModel
{
    /**
     * Command identifier;
     */
    public String command;

    /**
     * Id of the producer (sender).
     */
    public String producerId;

    /**
     * Id of the consumer (receiver).
     */
    public String consumerId;

    /**
     * Timestamp of the production of the command.
     */
    public long produceTimestamp;

    /**
     * Timestamp of the execution of the command.
     */
    public long executionTimestamp;

    public MovementCommand asMovementCommand()
    {
        if (command == null)
        {
            return MovementCommand.NOT_FOUND;
        }

        return MovementCommand.fromString(command);
    }
}
