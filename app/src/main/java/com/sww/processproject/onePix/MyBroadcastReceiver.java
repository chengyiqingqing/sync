package com.sww.processproject.onePix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by shaowenwen on 2018/1/9.
 */

public abstract class MyBroadcastReceiver extends BroadcastReceiver implements IBroadcast{

    private static final String TAG = "MyBroadcastReceiver";
    IBroadcast iBroadcast;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.e(TAG, "onReceive: on" );
            screenOn();
        }else {
            Log.e(TAG, "onReceive: off" );
            screenOff();
        }
    }

    @Override
    public abstract void screenOn();

    @Override
    public abstract void screenOff();
}
