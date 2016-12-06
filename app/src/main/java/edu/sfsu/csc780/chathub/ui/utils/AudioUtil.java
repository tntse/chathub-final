package edu.sfsu.csc780.chathub.ui.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by david on 12/5/16.
 */

public class AudioUtil {
    private static final String LOG_TAG = AudioUtil.class.getSimpleName();
    private static String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static int GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int REQUEST_CODE = 200;
    private static final String[] AUDIO_PERMISSIONS =
            {RECORD_AUDIO};

    public static void startAudioListener(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, RECORD_AUDIO) !=
                GRANTED ) {
            Log.d(LOG_TAG, "requesting permissions for starting");
            ActivityCompat.requestPermissions(activity, AUDIO_PERMISSIONS, REQUEST_CODE);
        }
    }
}
