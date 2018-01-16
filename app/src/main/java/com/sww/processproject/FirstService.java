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
public class FirstService extends Service {
    private Timer timer;
    private static int count=0;
    //两个对象，一个MyBinder,一个MyConn;
    private MyBinder binder;
    private MyConn conn;

    private static final String TAG = "sww";
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
        if(conn==null)
            conn = new MyConn();
        timer=new Timer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startService(new Intent(this,MyJobDaemonService.class));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "run: First "+(++count) );
            }
        },1000,2000);
        Log.e(TAG, "First 执行onStartCommand" );
        //前台服务。提高当前service的优先级。
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(250, builder.build());
        //开启第二服务。
//        startService(new Intent(this,SecondService.class));
        //绑定第二服务。
        FirstService.this.bindService(new Intent(this,SecondService.class),conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    //作为进程间通信的桥梁。
    class MyBinder extends ProcessService.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return "FirstService";
        }
    }

    //相当于一个监听的功能。
    class  MyConn implements ServiceConnection {
        //两个服务绑定成功时回调。
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 与远程服务通信
            ProcessService process = ProcessService.Stub.asInterface(iBinder);
            try{
                Log.i(TAG, "First连接" + process.getServiceName() + "服务成功");
            }catch (Exception e){

            }
        }
        //两个服务断开连接时回调，即为绑定者死去，此时执行救活逻辑。
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, " First监听到Second死去" );
            // 启动FirstService
            FirstService.this.startService(new Intent(FirstService.this,SecondService.class));
            //绑定FirstService
            FirstService.this.bindService(new Intent(FirstService.this,SecondService.class),conn, Context.BIND_AUTO_CREATE);
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
