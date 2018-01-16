package com.sww.processproject;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

/**
 * Created by sww on 2017/1/3.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobDaemonService extends JobService {
    //服务id对吗？
    private int kJobId = 0;
    private static final String TAG = "sww";
    /**
     * 1.把作业发送到任务调度中去。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyJobDaemonService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_STICKY;
    }

    //满足JobInfo之后，才能采取的措施。
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("sww", "Job执行了onStartJob方法");
        boolean isLocalServiceWork = isServiceWork(this, "com.cxmscb.cxm.processproject.FirstService");
        boolean isRemoteServiceWork = isServiceWork(this, "com.cxmscb.cxm.processproject.SecondService");
        if(!isLocalServiceWork||
                !isRemoteServiceWork){
            this.startService(new Intent(this,FirstService.class));

            this.startService(new Intent(this,SecondService.class));

            Log.i("onStartJob", "启动FirstService SecondService");
        }else{
            Log.e(TAG, "onStartJob走了:但是没启动Service！！！ " );
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("sww", "Job执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        JobScheduler js =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result=js.schedule(t);
        Log.e("sww", "result  ---  scheduleJob:   "+result+"  ----  JobId  -- "+kJobId );
    }

    public JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, MyJobDaemonService.class));
        //任何网络
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        //重启之后执行。
        builder.setPersisted(true);
        //在充电是执行。
        builder.setRequiresCharging(false);
        //设备空闲时。
        builder.setRequiresDeviceIdle(false);
        //间隔100毫秒 执行一次
        builder.setPeriodic(100);
        //最后再啰嗦一句，它们取并集。
        return builder.build();
    }

    // 判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
