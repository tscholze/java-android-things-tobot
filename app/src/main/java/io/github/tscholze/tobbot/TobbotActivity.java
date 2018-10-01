package io.github.tscholze.tobbot;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.tscholze.tobbot.listener.MovementRequestListener;
import io.github.tscholze.tobbot.managers.BeepToneManager;
import io.github.tscholze.tobbot.managers.CapButtonsManager;
import io.github.tscholze.tobbot.managers.MovementManager;
import io.github.tscholze.tobbot.managers.RemoteCommandManager;
import io.github.tscholze.tobbot.managers.WebServerManager;
import io.github.tscholze.tobbot.utils.CameraHandler;
import io.github.tscholze.tobbot.utils.MovementCommand;

/**
 * Vehicles main activity.
 * USed as an centralized starting point for calling and connecting manager, services and servers.
 */
public class TobbotActivity extends Activity implements MovementRequestListener, OnImageAvailableListener
{
    /**
     * Unique TAG.
     */
    private static final String TAG = TobbotActivity.class.getSimpleName();

    /**
     * Size of the taken image.
     */
    private static final Size MODEL_IMAGE_SIZE = new Size(640, 480);

    /**
     * Instance of the assigned movement manager.
     */
    private MovementManager movementManager;

    /**
     * Instance of assigned remote command manager.
     */
    private RemoteCommandManager remoteCommandManager;

    /**
     * Instance of the assigned captive buttons manager.
     * It will map button touches to movements.
     */
    private CapButtonsManager capButtonsManager;

    /**
     * Instance of the assigned beep tone manager.
     * It will play beep sounds.
     */
    private BeepToneManager beepToneManager;

    /**
     * Instance of the assigned web server manger to handle the locally hosted website.
     * It will map webserver requests to movements
     */
    private WebServerManager webserverManager;

    /**
     * Contains the current command.
     */
    private MovementCommand currentCommand = MovementCommand.STOP;

    /**
     * Indicates if all activity features are ready to use.
     */
    private AtomicBoolean isReady = new AtomicBoolean(false);

    private CameraHandler cameraHandler;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private Runnable initializeOnBackground = new Runnable()
    {
        @Override
        public void run()
        {
            cameraHandler = CameraHandler.getInstance();
            try
            {
                Log.d(TAG, "asdadadssd");
                cameraHandler.initializeCamera(TobbotActivity.this, backgroundHandler, MODEL_IMAGE_SIZE, TobbotActivity.this);
                CameraHandler.dumpFormatInfo(TobbotActivity.this);
            }
            catch (CameraAccessException e)
            {
                throw new RuntimeException(e);
            }

            setReady(true);
        }
    };

    private Runnable backgroundTakePictureHandler = new Runnable()
    {
        @Override
        public void run()
        {
            cameraHandler.takePicture();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobbot);

        // Init managers
        movementManager = new MovementManager();
        beepToneManager = new BeepToneManager();
        capButtonsManager = new CapButtonsManager(this);
        webserverManager = new WebServerManager(getApplicationContext(), true, this);
        remoteCommandManager = new RemoteCommandManager("1", this);

        // Setup camera
        backgroundThread = new HandlerThread("BackgroundThread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        backgroundHandler.post(initializeOnBackground);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        try
        {
            if (backgroundThread != null)
            {
                backgroundThread.quit();
            }

            if (cameraHandler != null)
            {
                cameraHandler.shutDown();
            }
        }
        catch (Throwable ignored)
        {

        }

        backgroundThread = null;
        backgroundHandler = null;

        capButtonsManager.destroy();
        capButtonsManager = null;

        webserverManager.destroy();
        webserverManager = null;

        beepToneManager.destroy();
        beepToneManager = null;

        remoteCommandManager.destroy();
        remoteCommandManager = null;

        movementManager.destroy();
        movementManager = null;
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
        return capButtonsManager.handle(keyCode);
    }

    @Override
    public Boolean requestMovement(MovementCommand command)
    {
        // TODO: Remove debug code execution
        // Use button tap as demo to trigger image capture
        startImageCapture();

        assert movementManager != null;
        assert beepToneManager != null;

        beepToneManager.shortBeep();

        if (currentCommand.equals(command))
        {
            return false;
        }

        currentCommand = command;

        // TODO: Maybe handle .NOT_FOUND state
        return movementManager.move(command);
    }

    @Override
    public void onImageAvailable(ImageReader reader)
    {
        Log.d(TAG, "Camera - onImageAvailable triggered");

        File file = null;
        BufferedWriter bufferedWriter = null;

        try (Image image = reader.acquireLatestImage())
        {
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byteBuffer.rewind();

            file = File.createTempFile("test.jpg", null, getApplicationContext().getCacheDir());

            if (!file.exists())
            {
                file.createNewFile();
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file, false));
            bufferedWriter.write(byteBuffer.asCharBuffer().array());

            Log.d(TAG, "Camera image listener wrote to file: " + file.getAbsolutePath());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Camera image saving failed with error; " + e.getLocalizedMessage());
        }
        finally
        {

            /* Not available.
            if (file != null)
            {
                file.close();
            }
            */

            if (bufferedWriter != null)
            {
                try
                {
                    bufferedWriter.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setReady(boolean ready)
    {
        isReady.set(ready);
        // TODO: Inform the user
    }

    private void startImageCapture()
    {
        boolean isReady = this.isReady.get();

        if (isReady)
        {
            backgroundHandler.post(backgroundTakePictureHandler);
        }
        else
        {
            Log.i(TAG, "Sorry, processing hasn't finished. Try again in a few seconds");
        }
    }
}
