package io.github.tscholze.tobbot;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import io.github.tscholze.tobbot.managers.CapButtonsManager;
import io.github.tscholze.tobbot.managers.MovementManager;
import io.github.tscholze.tobbot.managers.WebServerManager;
import io.github.tscholze.tobbot.utils.MovementCommand;
import io.github.tscholze.tobbot.listener.MovementRequestListener;

/**
 * Vehicles main activity.
 * USed as an centralized starting point for calling and connecting manager, services and servers.
 */
public class TobbotActivity extends Activity implements MovementRequestListener
{
    private static final String TAG = TobbotActivity.class.getSimpleName();

    /**
     * Instance of the assigned movement manager.
     */
    private MovementManager movementManager;

    /**
     * Instance of the assigned captive buttons manager.
     * It will map button touches to movements.
     */
    private CapButtonsManager capButtonsManager;

    /**
     * Instance of the assigned web server manger to handle the locally hosted website.
     * It will map webserver requests to movements
     */
    private WebServerManager webserverMananger;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobbot);

        // Init managers
        movementManager = new MovementManager();
        capButtonsManager = new CapButtonsManager(this);
        webserverMananger = new WebServerManager(getApplicationContext(), true, this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        capButtonsManager.destroy();
        capButtonsManager = null;

        webserverMananger.destroy();
        webserverMananger = null;

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

        // TODO: Maybe handle .NOT_FOUND state
        return movementManager.move(command);
    }
}
