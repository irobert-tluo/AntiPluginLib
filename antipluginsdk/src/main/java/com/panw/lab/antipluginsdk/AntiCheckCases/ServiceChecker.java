package com.panw.lab.antipluginsdk.AntiCheckCases;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.panw.lab.antipluginsdk.AntiCheckCases.Dummy.DummyRemoteService;
import com.panw.lab.antipluginsdk.AntiCheckCases.Dummy.DummyService;

import java.util.Iterator;
import java.util.List;

/**
 * Created by tluo on 1/15/17.
 */

public class ServiceChecker {
    private Context ctx;
    public boolean isPlugin(
            Context context,
            ActivityManager manager) {
        this.ctx = context;
        Thread t1 = new ServiceChecker.task_thread(ctx, this, manager);
        t1.start();
        return false;
    }

    private boolean checkLocalService(Context context, ActivityManager manager){
        Class myService = DummyService.class;
        String service_name = myService.getName();
        this.startSvc(context, myService);
        boolean serviceNameScan = this.ScanServiceName(manager, service_name);
        return serviceNameScan;
    }

    private boolean checkRemoteService(Context context, ActivityManager manager){
        Class myService = DummyRemoteService.class;
        String service_name = myService.getName();
        this.startSvc(context, myService);
        boolean serviceNameScan = this.ScanServiceName(manager, service_name);
        return serviceNameScan;
    }

    private boolean startSvc(Context context, Class myService){
        Log.w("Anti", "ServiceChecker:startSvc:" + myService.getName());
        context.startService(new Intent(context, myService));
        return false;
    }

    public boolean ScanServiceName(ActivityManager manager, String service_name){
        Log.w("Anti", "+[Service-Check] [ScanServiceName] Begin");
        boolean isPlugin = true;
        List<ActivityManager.RunningServiceInfo> serviceList = manager.getRunningServices(100);
        for (Iterator<ActivityManager.RunningServiceInfo> iterator = serviceList.iterator(); iterator.hasNext();) {
            ActivityManager.RunningServiceInfo serviceInfo = iterator.next();
            if(!serviceInfo.service.toString().contains("com.google")
                    && !serviceInfo.service.toString().contains("com.android")
                    && !serviceInfo.service.toString().contains("android.hardware")) {
                Log.v("Anti","      ServiceChecker - ScanServices:" + serviceInfo.service.toString());
            }
            if(serviceInfo.service.toString().contains(service_name)){
                isPlugin = false;
            }
        }
        phoneback(ctx, "result", "", ""+isPlugin,"ScanServices");
        return isPlugin;
    }

    static public void phoneback(Context ctx, String key, String detail, String result, String task){
        Intent intent5 = new Intent("com.android.broadcast.RESULT");
        intent5.putExtra("key", key);
        intent5.putExtra("detail", detail);
        intent5.putExtra("result", result);
        intent5.putExtra("task", task);
        ctx.sendBroadcast(intent5);
    }
    class task_thread extends Thread {
        private Context ctx;
        private ServiceChecker mine;
        private ActivityManager am;
        public task_thread(Context ctx, ServiceChecker mine, ActivityManager am){
            this.ctx = ctx;
            this.mine = mine;
            this.am = am;
        }
        public void run() {
            this.mine.ScanServiceName(am, "AntiCheckCases.AntiCheckMainService");
        }
    }
}
