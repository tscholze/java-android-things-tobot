package io.github.tscholze.tobbot;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import io.github.tscholze.tobbot.listener.MovementRequestListener;
import io.github.tscholze.tobbot.managers.BeepToneManager;
import io.github.tscholze.tobbot.managers.CapButtonsManager;
import io.github.tscholze.tobbot.managers.MovementManager;
import io.github.tscholze.tobbot.managers.RemoteCommandManager;
import io.github.tscholze.tobbot.managers.WebServerManager;
import io.github.tscholze.tobbot.utils.MovementCommand;

/**
 * Vehicles main activity.
 * USed as an centralized starting point for calling and connecting manager, services and servers.
 */
public class TobbotActivity extends Activity implements MovementRequestListener
{
    /**
     * Unique TAG.
     */
    private static final String TAG = TobbotActivity.class.getSimpleName();

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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

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
}
