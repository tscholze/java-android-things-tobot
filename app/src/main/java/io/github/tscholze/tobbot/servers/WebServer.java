package io.github.tscholze.tobbot.servers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import io.github.tscholze.tobbot.utils.MovementCommand;
import io.github.tscholze.tobbot.utils.MovementRequestListener;
import io.github.tscholze.tobbot.utils.VehicleCommand;


/**
 * App's web server that provides the capabilities
 * to control the TobboT via http requests.
 */
public class WebServer extends NanoHTTPD
{
    private static final String TAG = WebServer.class.getSimpleName();

    /**
     * Callback listener to connect to the host activity.
     */
    private MovementRequestListener movementRequestListener;

    /**
     * Default port for browsing the hosted web page.
     */
    private static final int DEFAULT_PORT = 8080;

    /**
     * Default http GET parameter for the direction input.
     */
    private static final String DIRECTION_GET_KEY = "d";

    /**
     * The web content that will be delivered.
     */
    private String content;

    /**
     * Constructor.
     * Will set the web server port to the default value.
     *
     * @param context Application's context.
     * @param autostart Defines if the server should autos tart serving.
     * @param movementRequestListener Attached movement listener.
     */
    public WebServer(Context context, boolean autostart, MovementRequestListener movementRequestListener)
    {
        super(DEFAULT_PORT);
        setupContent(context);

        if(autostart)
        {
            startServing();
        }

        this.movementRequestListener = movementRequestListener;
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        Map<String, String> parameters = session.getParms();
        String direction = parameters.get(DIRECTION_GET_KEY);

        if(direction != null)
        {
            movementRequestListener.requestMovement(MovementCommand.fromString(direction));
        }

        assert content != null;
        return newFixedLengthResponse(content);
    }

    /**
     * Starts the serving of the web server on the specified port.
     *
     * @return True if the starting was successful
     */
    @SuppressWarnings("all")
    public boolean startServing()
    {
        try
        {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.d(TAG, "Started web server on port: " + DEFAULT_PORT);
            return true;
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not start webserver: " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Stops the serving of the web server
     */
    public void stopServing()
    {
        stop();
    }

    private void setupContent(Context context)
    {
        Resources resources = context.getResources();
        InputStream ins = resources.openRawResource(resources.getIdentifier("index", "raw", context.getPackageName()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try
        {
            while ((len = ins.read(buf)) != -1)
            {
                outputStream.write(buf, 0, len);
            }

            outputStream.close();
            ins.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getLocalizedMessage());
        }

        content = outputStream.toString();
    }
}
