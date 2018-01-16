package com.sww.processproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


import com.sww.processproject.aidl.ProcessService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sww on 2017/1/3.
 */
public class SecondService extends Service {

    private Timer timer;
    private static int count=0;
    //同第一个Service的逻辑是一样的。（实例化在onCreate里面。而且只是实例化一次。）
    private MyBinder binder;
    private MyConn conn;

    private static final String TAG = "sww";
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //初始化操作。binder,conn;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Second ----- onCreate: " );
        binder = new MyBinder();
        if(conn==null)
            conn = new MyConn();
        timer=new Timer();
    }

    //设置前台服务，提高优先级。（该方法具有什么样的性质，应该执行什么的曹组合适。）
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        super.onStartCommand(intent, flags,startId);
        startService(new Intent(this,MyJobDaemonService.class));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "run: Second "+(++count) );
            }
        },1000,2000);
        Log.e(TAG, "Second  -----  onStartCommand: " );
        //开前台服务，提高服务的优先级。
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(250, builder.build());
        //开启ThirdService;
//        startService(new Intent(this,FirstService.class));
        //绑定FirstService.
        SecondService.this.bindService(new Intent(this,FirstService.class),conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    class MyBinder extends ProcessService.Stub{

        @Override
        public String getServiceName() throws RemoteException {
            return "SecondService";
        }
    }

    class  MyConn implements ServiceConnection {

        //连接成功之后回调。（不做任何操作。因为不需要IBinder交流数据。）
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ProcessService processService=ProcessService.Stub.asInterface(iBinder);
            try{
                Log.i(TAG, "second连接" + processService.getServiceName() + "服务成功");
            }catch (Exception e){

            }
        }

        //解绑之后回调。这里让它解绑未遂，重新拉起死去的service。
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, " Second监听到First死去" );
            // 启动FirstService
            SecondService.this.startService(new Intent(SecondService.this,FirstService.class));
            //绑定FirstService
            SecondService.this.bindService(new Intent(SecondService.this,FirstService.class),conn, Context.BIND_IMPORTANT);
        }

    }

    public static class  InnerService extends Service{
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e("InnerService","Service");
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(250, builder.build());
            //这里将它在0.1s之后取消掉就可以了。
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(250);
                    stopSelf();
                }
            },100);
        }
    }

}
