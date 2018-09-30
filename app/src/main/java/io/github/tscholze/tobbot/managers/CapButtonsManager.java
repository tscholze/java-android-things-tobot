package io.github.tscholze.tobbot.managers;

import android.util.Log;

import com.google.android.things.contrib.driver.cap12xx.Cap12xx;
import com.google.android.things.contrib.driver.cap12xx.Cap12xxInputDriver;
import com.google.android.things.contrib.driver.cap1xxx.Cap1xxx;
import com.google.android.things.contrib.driver.cap1xxx.Cap1xxxInputDriver;

import java.io.IOException;

import io.github.tscholze.tobbot.listener.MovementRequestListener;
import io.github.tscholze.tobbot.utils.MovementCommand;
import io.github.tscholze.tobbot.utils.VehicleUtils;

/**
 * The manager is responsible for providing the setup of the button driver and
 * the mapping from triggered button key down events to a vehicle movement.
 */
public class CapButtonsManager
{
    /**
     * Unique TAG.
     */
    private final String TAG = CapButtonsManager.class.getSimpleName();

    /**
     * Key code for the Button "1" on the Explorer HAT.
     * Raw value: 12
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int BUTTON_1_KEYCODE = 12;

    /**
     * Key code for the Button "2" on the Explorer HAT.
     * Raw value: 13
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int BUTTON_2_KEYCODE = 13;

    /**
     * Key code for the Button "3" on the Explorer HAT.
     * Raw value: 14
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int BUTTON_3_KEYCODE = 14;

    /**
     * Key code for the Button "4" on the Explorer HAT.
     * Raw value: 15
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int BUTTON_4_KEYCODE = 15;

    /**
     * Callback listener to connect to the host activity.
     */
    private MovementRequestListener movementRequestListener;

    /**
     * Instance of the assigned captive button driver.
     */
    private Cap1xxxInputDriver inputDriver;

    /**
     * Initialing that assignes a callback listener.
     *
     * @param movementRequestListener Callback listener that will be assigned.
     */
    public CapButtonsManager(MovementRequestListener movementRequestListener)
    {
        this.movementRequestListener = movementRequestListener;

        try
        {
            inputDriver = new Cap1xxxInputDriver("I2C1", null, Cap12xx.Configuration.CAP1208, VehicleUtils.keyCodes);
            inputDriver.setRepeatRate(Cap1xxx.REPEAT_DISABLE);
            inputDriver.setMultitouchInputMax(1);
            inputDriver.register();
        }
        catch (IOException e)
        {
            Log.w(TAG, "Unable to open driver connection", e);
        }
    }

    /**
     * Maps Key down events in contact of the Explorer HAT captive buttons to vehicle movements.
     *
     * @param keyCode Key code of the triggered button.
     * @return True if the key code was handled successfully.
     */
    public boolean handle(int keyCode)
    {
        Log.d(TAG, "Pressed key: " + keyCode);

        if (movementRequestListener == null)
        {
            return false;
        }

        switch (keyCode)
        {
            case BUTTON_1_KEYCODE:
                return movementRequestListener.requestMovement(MovementCommand.FORWARD);

            case BUTTON_2_KEYCODE:
                return movementRequestListener.requestMovement(MovementCommand.LEFT);

            case BUTTON_3_KEYCODE:
                return movementRequestListener.requestMovement(MovementCommand.RIGHT);

            case BUTTON_4_KEYCODE:
                return movementRequestListener.requestMovement(MovementCommand.STOP);
        }

        return false;
    }

    /**
     * Destroys the manager.
     */
    public void destroy()
    {
        if (inputDriver == null)
        {
            return;
        }

        inputDriver.unregister();

        try
        {
            inputDriver.close();
        }
        catch (IOException e)
        {
            Log.w(TAG, "Unable to close touch driver", e);
        }
        finally
        {
            inputDriver = null;
        }
    }
}
