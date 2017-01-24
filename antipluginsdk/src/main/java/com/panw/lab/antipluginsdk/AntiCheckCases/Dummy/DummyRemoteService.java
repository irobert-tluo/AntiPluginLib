package com.panw.lab.antipluginsdk.AntiCheckCases.Dummy;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class DummyRemoteService extends Service {
    int myPid = -1;
    public DummyRemoteService() {
    }

    @Override
    public void onCreate() {
        this.myPid = Process.myPid();
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("Anti","DummyRemoteService -> onCreate, Thread: " + Thread.currentThread().getName());
        ActivityManager.RunningAppProcessInfo myProcessInfo = getRunningAppProcessInfo(manager, myPid);
        Log.i("Anti","PID:["+ this.myPid + "] ProcName:[" + myProcessInfo.processName + "]");
        super.onCreate();
    }

    public ActivityManager.RunningAppProcessInfo getRunningAppProcessInfo(ActivityManager manager, int pid){
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses())
        {
            if (processInfo.pid == pid)
            {
                return processInfo;
            }
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Anti", "DummyRemoteService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
