package com.panw.lab.antipluginsdk.AntiCheckCases.Dummy;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class DummyReceiver extends BroadcastReceiver {

    public DummyReceiver() {

    }
    public DummyReceiver(Context c) {
        ct=c;
        receiver=this;
    }
    NotificationManager mn=null;
    Notification notification=null;
    Context ct=null;
    DummyReceiver receiver;

    public void registerAction(String action){
        IntentFilter filter=new IntentFilter();
        filter.addAction(action);
        ct.registerReceiver(receiver, filter);
    }
    public void unregisterAction_all() {
        ct.unregisterReceiver(receiver);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.v("Anti", "      DummyReceiver::onReceive: Action[" + intent.getAction().toString() + "] Data[" + intent.getData() + "]");
        phoneback(context, "callback", "DummyReceiver-"+intent.getAction(),  "1");
    }
    public void phoneback(Context ctx, String key, String detail, String result){
        Intent intent5 = new Intent("com.android.broadcast.RESULT");
        intent5.putExtra("key", key);
        intent5.putExtra("detail", detail);
        intent5.putExtra("result", result);
        ctx.sendBroadcast(intent5);
    }
}
