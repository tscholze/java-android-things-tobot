package io.github.tscholze.tobbot.managers;

import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * The manager is responsible for providing different kinds of beep sounds.
 */
public class BeepToneManager
{
    /**
     * Underlying tone generator.
     */
    private ToneGenerator toneGenerator;

    /**
     * Initialises the underlying tone generator with 100% volume.
     */
    public BeepToneManager()
    {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    }

    /**
     * Plays a short beep tone.
     */
    public void shortBeep()
    {
        if(toneGenerator == null)
        {
            return;
        }

        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000);
    }

    /**
     * Destroys the manager.
     */
    public void destroy()
    {
        if(toneGenerator == null)
        {
            return;
        }

        toneGenerator.stopTone();
        toneGenerator.release();
        toneGenerator = null;
    }
}
