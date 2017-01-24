package com.panw.lab.antipluginsdk.AntiCheckCases;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiCheckMainService extends Service {
    private BroadcastCheck broadcastCheck;
    private ServiceChecker serviceChecker;
    private PackageChecker packageChecker;

    private BroadcastReceiver mReceiver;
    private Map<String, Boolean> pluginResult = new HashMap<String, Boolean>();
    private Map<String, String> pluginDetail = new HashMap<String, String>();
    private List<Intent> receivedIntent = new ArrayList<Intent>();
    private List<String> receivedIntentStr = new ArrayList<String>();

    private int num_of_test = 5;

    public AntiCheckMainService() {
    }

    private void dump_test_result() {
        for (Map.Entry<String, Boolean> entry : pluginResult.entrySet()){
            Log.v("Anti", entry.getKey() + "/" + entry.getValue());
            if(entry.getValue()) {
                terminate_app();
            }
        }
    }

    private void terminate_app() {
        System.exit(0);
    }

    public boolean searchIntentHistory(String x){
        Log.v("Anti", "      {searchIntentHistory} => " + receivedIntentStr.toString());
        return receivedIntentStr.contains(x);
    }

    public void prepareReceiver(){
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("com.android.broadcast.RESULT")) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        String key = extras.getString("key");

                        // Receive C2 Intent
                        if(key.equals("c2")) {
                            String result = extras.getString("result");
                            String task = extras.getString("task");
                            String target_value = extras.getString("detail");
                            boolean curResult = searchIntentHistory(target_value);
                            if(result.equals("1")) {
                                Log.v("Anti", "      [C2]  Should Receive Intent [" + target_value + "]");
                                curResult = !curResult;
                            } else {
                                Log.v("Anti", "      [C2]  Should NOT Receive Intent [" + target_value + "]");
                            }
                            pluginResult.put(task, curResult);
                            Log.w("Anti", "+[Task]:" + task + " - [isPlugin]:" + curResult);
                            if(pluginResult.size() == num_of_test) {
                                dump_test_result();
                            }

                        // Receive Callback Intent
                        } else if(key.equals("callback")) {
                            Log.v("Anti", "      Receive Callback: [" + extras.getString("detail") + "]");
                            receivedIntent.add(intent);
                            receivedIntentStr.add(extras.getString("detail"));

                        // Receive Task Result
                        } else if(key.equals("result")) {
                            String result = extras.getString("result");
                            String task = extras.getString("task");
                            pluginResult.put(task, Boolean.parseBoolean(result));
                            Log.w("Anti", "+[Task]:" + task + " - [isPlugin]:" + result);
                            if(pluginResult.size() == num_of_test) {
                                dump_test_result();
                            }
                        } else {
                            Log.v("Anti", "      key   : "+key);
                            String detail = extras.getString("detail");
                            Log.v("Anti", "      detail: "+detail);
                            String result = extras.getString("result");
                            Log.v("Anti", "      result: "+result);
                        }
                    }
                } else {
                    Log.v("Anti", "      AntiCheckMainService -> onReceive action: "+action);
                }
            }
        };
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.android.broadcast.RESULT");
        this.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart (Intent intent, int startId){
        Log.v("Anti", "AntiCheckMainService -> onStart, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction() == null) {
            super.onStartCommand(intent, flags, startId);
            prepareReceiver();
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            PackageManager packageManager = this.getPackageManager();
            serviceChecker = new ServiceChecker();
            serviceChecker.isPlugin(this, activityManager);
            packageChecker = new PackageChecker();
            packageChecker.isPlugin(this, packageManager);
            broadcastCheck = new BroadcastCheck();
            broadcastCheck.isPlugin(this.getApplicationContext());
        } else {
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
