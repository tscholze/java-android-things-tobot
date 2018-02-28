package io.github.tscholze.tobbot.managers;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by tscholze on 28.02.18.
 */

public class WebServer extends NanoHTTPD
{
    private static final String TAG = WebServer.class.getSimpleName();
    private static final int DEFAULT_PORT = 8080;


    public WebServer()
    {
        super(DEFAULT_PORT);
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null)
        {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        }
        else
        {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

    /**
     * Starts the serving of the web server on the specified port.
     *
     * @return True if the starting was successful
     */
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
}
