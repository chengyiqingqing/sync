package com.sww.processproject.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by shaowenwen on 2018/1/8.
 */

public class SyncService extends Service {

    private static final Object syncLock = new Object();
    private static SyncAdapter syncAdapter = null;
    private static final String TAG = "sww";

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    public static Handler  handler=new Handler();
    class SyncAdapter extends AbstractThreadedSyncAdapter {


        public Context context;

        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
            this.context=context;
        }
        //do sync here
        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
             //kill app ，clear app data ； force stop三者都可以活过来。(已验证：moto)
            Log.v(TAG,"onPerformSync");
            //1.验证，它自己起来的。和下面这段代码没有关系。()
            /*Intent intent = new Intent();
            ComponentName componentName = new ComponentName(account.type, account.name);
            intent.setComponent(componentName);
            getContext().startService(intent);*/
            //2.设置延时强制执行。
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: 接着手动同步perform" );
                    startAccountSync(context);
                }
            },5000);
            //3.在这里设置周期轮询
        }
    }

    /**
     * 利用帐号同步机制拉活
     * @param context
     */
    public static void startAccountSync(final Context context){ //好了这样就成了10秒调用一次。
        Log.e(TAG, "startAccountSync: --- 0" );
        String accountType = "com.sww.processproject";
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = null;
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if(accounts.length>0){
            Log.e(TAG, "startAccountSync:  -- 1 " );
            account = accounts[0];
        }else {
            account = new Account("sww", accountType);
            Log.e(TAG, "startAccountSync:  -- 2 " );
        }

        final String authority = accountType;
        if(!accountManager.addAccountExplicitly(account,null,null)){//账号不存在，对该账号进行添加。
            Log.e(TAG, "startAccountSync:  -- 3" );
            //如果为true，那么下面的手动同步不生效。
            ContentResolver.setSyncAutomatically(account, authority, false);  //有网络时是否自动同步
            ContentResolver.requestSync(account, authority, new Bundle());
            //手动同步之后，再自动同步。或者延时5s再自动同步。
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    postRunning(context);
                }
            },2000);
           /* //自动同步的设置。
            long sync_interval = 2;//15*60s
            ContentResolver.setIsSyncable(account,authority,1);//是否同步；
            ContentResolver.setSyncAutomatically(account, authority, true);  //有网络时是否自动同步
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), sync_interval);//设置同步时间间隔*/
        }else {//账号存在，或者是其他的错误。
            Log.e(TAG, "startAccountSync:  -- 4" );
            //手动同步：（如果为true，那么下面的手动同步不生效。）
            ContentResolver.setSyncAutomatically(account, authority, false);  //有网络时是否自动同步
            ContentResolver.requestSync(account, authority, new Bundle());
            //手动同步之后，再自动同步。或者延时5s再自动同步。
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    postRunning(context);
                }
            },2000);
        }

    }

    public static void postRunning(Context context){
        String accountType = "com.sww.processproject";
        AccountManager accountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = null;
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if(accounts.length>0){
            account = accounts[0];
        }else {
            account = new Account("sww", accountType);
        }
        String authority = accountType;
        long sync_interval = 2;//15*60s
        ContentResolver.setIsSyncable(account,authority,1);//是否同步；
        ContentResolver.setSyncAutomatically(account, authority, true);  //有网络时是否自动同步
        ContentResolver.addPeriodicSync(account, authority, new Bundle(), sync_interval);//设置同步时间间隔
    }

}

