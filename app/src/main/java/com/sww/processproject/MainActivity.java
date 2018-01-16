package com.sww.processproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sww.processproject.account.SyncService;
import com.sww.processproject.onePix.OnePixActivity;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=MainActivity.this;
//        startServices();
        startOnePixActivity();
//        startAsynAccount();
    }

    private void startServices(){
        //双进程防杀死。
        startService(new Intent(mContext,FirstService.class));
        startService(new Intent(mContext,SecondService.class));
        //JobService保活。  它执行了，但是start和stop未必会被调度。
        startService(new Intent(mContext,MyJobDaemonService.class));
    }

    private void startOnePixActivity(){
        OnePixActivity.startOnePixActivity(mContext);
    }

    private void startAsynAccount(){
        //利用帐号同步机制  拉活
        SyncService.startAccountSync(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
