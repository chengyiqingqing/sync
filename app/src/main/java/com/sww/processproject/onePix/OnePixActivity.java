package com.sww.processproject.onePix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by shaowenwen on 2018/1/5.
 */

public class OnePixActivity extends AppCompatActivity {

    public static final String TAG = "sww";
    private BroadcastReceiver myBroadcast=null;

    public static void startOnePixActivity(Context context) {
        Intent intent = new Intent(context, OnePixActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate：一个像素Activity创建");
        Window window = getWindow();
        //放在左上角
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        //宽高设计为1个像素
        attributes.width = 1;
        attributes.height = 1;
        //起始坐标
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);
        registerMyBroadcast();
        checkScreenOn();
    }

    private void checkScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.d(getClass().getName(), "isScreenOn: " + isScreenOn);
        if (isScreenOn) {
            finish();
        }
    }

    public void registerMyBroadcast(){
        if (myBroadcast==null){
            myBroadcast=new MyBroadcast();
        }
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(myBroadcast,intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn();
        Log.e(TAG, "OnePixActivity --- onResume:" );

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "OnePixActivity --- onPause: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "OnePixActivity --- onStop: " );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "OnePixActivity --- onDestroy: " );
        unregisterReceiver(myBroadcast);
    }

    class MyBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.e(TAG, "onReceive: on" );
                OnePixActivity.this.finish();
            }else {
                Log.e(TAG, "onReceive: off" );
                OnePixActivity.startOnePixActivity(OnePixActivity.this);
            }
        }
    }

}