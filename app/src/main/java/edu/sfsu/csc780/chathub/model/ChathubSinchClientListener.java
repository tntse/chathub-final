package edu.sfsu.csc780.chathub.model;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

/**
 * Created by david on 12/5/16.
 */

public class ChathubSinchClientListener implements SinchClientListener {
    @Override
    public void onClientStarted(SinchClient sinchClient) {}

    @Override
    public void onClientStopped(SinchClient sinchClient) {}

    @Override
    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {}

    @Override
    public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {}

    @Override
    public void onLogMessage(int i, String s, String s1) {}
}