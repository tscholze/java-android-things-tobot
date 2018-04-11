package io.github.tscholze.tobbot.managers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.github.tscholze.tobbot.listener.MovementRequestListener;
import io.github.tscholze.tobbot.models.RemoteCommand;
import io.github.tscholze.tobbot.utils.MovementCommand;


/**
 * The manager is responsible for providing a bridge between Firebase real time database
 * and movement commands of the vehicle.
 */
public class RemoteCommandManager
{
    /**
     * Unique TAG.
     */
    private final String TAG = MovementManager.class.getSimpleName();

    /**
     * Firebase root node identifier
     */
    private static String ROOT_IDENTIFIER = "remote_commands";

    /**
     * Callback listener to connect to the host activity.
     */
    private MovementRequestListener movementRequestListener;

    /**
     * Assigned Firebase database instance.
     */
    private FirebaseDatabase database;

    /**
     * Assigned database reference to listen on.
     */
    private DatabaseReference reference;

    /**
     * The required consumer id to validate remote commands.
     */
    private String requiredConsumerId;

    /**
     * Constructor.
     * Will setup the manager with default values and starts the database change value
     * listing.
     *
     * @param requiredConsumerId Required consumer id to accepted remote commands.
     * @param movementRequestListener Attached movement listener.
     */
    public RemoteCommandManager(String requiredConsumerId, final MovementRequestListener movementRequestListener)
    {
        this.requiredConsumerId = requiredConsumerId;
        this.movementRequestListener = movementRequestListener;
        this.database = FirebaseDatabase.getInstance();
        this.reference = database.getReference(ROOT_IDENTIFIER);

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    RemoteCommand remoteCommand = postSnapshot.getValue(RemoteCommand.class);

                    // Check if remoteCommand is valid
                    // 1. It has to be set
                    // 2. It has no execution date
                    // 3. The consumer should be the current vehicle.
                    if (remoteCommand == null || remoteCommand.executionTimestamp != 0|| remoteCommand.consumerId != requiredConsumerId)
                    {
                        Log.d(TAG, "Ignored remote command");
                        continue;
                    }

                    MovementCommand command = remoteCommand.asMovementCommand();
                    Boolean success = movementRequestListener.requestMovement(command);

                    if (success)
                    {
                        remoteCommand.executionTimestamp = new Date().getTime();
                        FirebaseDatabaseManager.update(ROOT_IDENTIFIER, remoteCommand);
                    }
                    else
                    {
                        Log.e(TAG, "Could not execute remote command");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    /**
     * Sends a remote command to Firebase real time database.
     *
     * @param command Command to send.
     */
    public void sendCommand(RemoteCommand command)
    {
        String success = FirebaseDatabaseManager.create(ROOT_IDENTIFIER, command);
        Log.d(TAG, "Sending of a command was successful: " + success);
    }

    /**
     * Destroys all assigned references.
     * Use this method in onDestroy().
     */
    public void destroy()
    {
        requiredConsumerId = null;
        reference = null;
        database = null;
        movementRequestListener = null;
    }
}
