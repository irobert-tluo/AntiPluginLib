package com.panw.lab.antipluginsdk.AntiCheckCases.Dummy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class DummyService extends Service {
    public DummyService() {
    }

    @Override
    public void onCreate() {
        Log.i("Anti","DummyService -> onCreate, Thread: " + Thread.currentThread().getName() +"PID:"+ Process.myPid());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Anti", "DummyService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
