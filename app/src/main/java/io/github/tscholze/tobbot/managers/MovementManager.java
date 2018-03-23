package io.github.tscholze.tobbot.managers;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import io.github.tscholze.tobbot.utils.MovementCommand;

/**
 * The manager is responsible for providing movement functionality.
 * Check GPIO names in case of misbehaviour.
 *
 * The source is heavily inspired by:
 *  - Author: Francesco Azzola
 *  - Link: https://www.survivingwithandroid.com/2017/12/building-a-remote-controlled-car-using-android-things-gpio.html
 *
 *  Source of GPIO pin mapping:
 *  - Author: Microsoft
 *  - Link: https://docs.microsoft.com/en-us/windows/iot-core/learn-about-hardware/pinmappings/pinmappingsrpi
 */
public class MovementManager
{
    private final String TAG = MovementManager.class.getSimpleName();

    @SuppressWarnings("FieldCanBeLocal")
    private final String LEFT_MOTOR_FWD_GPIO_NAME = "BCM19";
    @SuppressWarnings("FieldCanBeLocal")
    private final String LEFT_MOTOR_BWD_GPIO_NAME = "BCM20";
    @SuppressWarnings("FieldCanBeLocal")
    private final String RIGHT_MOTOR_FWD_GPIO_NAME = "BCM21";
    @SuppressWarnings("FieldCanBeLocal")
    private final String RIGHT_MOTOR_BWD_GPIO_NAME = "BCM26";

    private Gpio rightMotorFwd;
    private Gpio rightMotorBwd;
    private Gpio leftMotorFwd;
    private Gpio leftMotorBwd;

    /**
     * Instantiates and configures all movement related parts like motors.
     */
    public MovementManager()
    {
        // Map Pins to motors
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        try
        {
            rightMotorFwd = peripheralManager.openGpio(LEFT_MOTOR_FWD_GPIO_NAME);
            rightMotorBwd = peripheralManager.openGpio(LEFT_MOTOR_BWD_GPIO_NAME);
            leftMotorFwd = peripheralManager.openGpio(RIGHT_MOTOR_FWD_GPIO_NAME);
            leftMotorBwd = peripheralManager.openGpio(RIGHT_MOTOR_BWD_GPIO_NAME);

            // Default configurations
            rightMotorFwd.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            rightMotorBwd.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            leftMotorFwd.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            leftMotorBwd.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        }
        catch (IOException e)
        {
            System.out.print(e.getLocalizedMessage());
        }
    }

    /**
     * Moves the vehicle by given command.
     * @param command Movement command
     * @return True if command was executed successfully
     */
    public boolean move(MovementCommand command)
    {
        switch (command)
        {
            case FORWARD:
                return moveForward();

            case BACKWARD:
                return moveBackwards();

            case LEFT:
                return moveLeft();

            case RIGHT:
                return moveRight();

            default:
                return stop();
        }
    }

    /**
     * Indicates if the vehicle is moving.
     * A vehicle is moving, if at least one motor is currently spinning.
     *
     * @return True if at least one motor is currently spinning.
     */
    public boolean getIsMoving()
    {
        try
        {
            return (leftMotorFwd.getValue() || leftMotorBwd.getValue()|| rightMotorFwd.getValue() || leftMotorBwd.getValue());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sets the running movement direction to forward.
     * @return True if movement direction setting was successful.
     */
    public boolean moveForward()
    {
        Log.i(TAG, "Moving forward");
        return setMotorGpioValues(true, false, true, false);
    }

    /**
     * Sets the running movement direction to right.
     * @return True if movement direction setting was successful.
     */
    public boolean moveRight()
    {
        return setMotorGpioValues(false, true, true, false);
    }

    /**
     * Sets the running movement direction to backwards.
     * @return True if movement direction setting was successful.
     */
    public boolean moveBackwards()
    {
        return setMotorGpioValues(false, true, false, true);
    }

    /**
     * Sets the running movement direction to left.
     * @return True if movement direction setting was successful.
     */
    public boolean moveLeft()
    {
        return setMotorGpioValues(true, false, false, true);
    }

    /**
     * Stops the running movement.
     * @return True if movement direction setting was successful.
     */
    public boolean stop()
    {
        return setMotorGpioValues(false, false, false,false);
    }

    /**
     * Stops and safely closes all GPIO pin connections.
     * Use this method on destroy.
     */
    public void destroy()
    {
        stop();

        try
        {
            rightMotorFwd.close();
            rightMotorBwd.close();
            leftMotorFwd.close();
            leftMotorBwd.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean setMotorGpioValues(boolean oneFwd, boolean oneBwd, boolean twoFwd, boolean twoBwd)
    {
        try
        {
            rightMotorFwd.setValue(oneFwd);
            rightMotorBwd.setValue(oneBwd);
            leftMotorFwd.setValue(twoFwd);
            leftMotorBwd.setValue(twoBwd);

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return false;
        }
    }
}

