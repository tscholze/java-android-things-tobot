package io.github.tscholze.tobbot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.cap12xx.Cap12xx;
import com.google.android.things.contrib.driver.cap12xx.Cap12xxInputDriver;

import java.io.IOException;

import io.github.tscholze.tobbot.managers.MovementManager;
import io.github.tscholze.tobbot.utils.VehicleUtils;


public class TobbotActivity extends Activity
{
    private static final String TAG = TobbotActivity.class.getSimpleName();

    private MovementManager movementManager;
    private Cap12xxInputDriver inputDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobbot);

        setupMovementManager();
        setupCapacitiveTouchButtons();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        destroyCapacitiveTouchButtons();
        destroyMovementManager();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        movementManager.stop();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.d(TAG, "Pressed key: " + keyCode);

        if (movementManager.getIsMoving())
        {
            movementManager.stop();
        }
        else
        {
            movementManager.moveForward();
        }

        return true;
    }

    private void setupCapacitiveTouchButtons()
    {
        try
        {
            inputDriver = new Cap12xxInputDriver("I2C1", null, Cap12xx.Configuration.CAP1208, VehicleUtils.keyCodes);

            inputDriver.setRepeatRate(Cap12xx.REPEAT_DISABLE);
            inputDriver.setMultitouchInputMax(1);
            inputDriver.register();
        }
        catch (IOException e)
        {
            Log.w(TAG, "Unable to open driver connection", e);
        }
    }

    private void setupMovementManager()
    {
        try
        {
            movementManager = new MovementManager();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void destroyCapacitiveTouchButtons()
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


    private void destroyMovementManager()
    {
        if (movementManager == null)
        {
            return;
        }

        movementManager.close();
        movementManager = null;
    }
}
