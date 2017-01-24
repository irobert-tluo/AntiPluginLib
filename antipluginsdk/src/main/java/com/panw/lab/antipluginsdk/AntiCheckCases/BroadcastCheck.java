package com.panw.lab.antipluginsdk.AntiCheckCases;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import com.panw.lab.antipluginsdk.AntiCheckCases.Dummy.DummyReceiver;

/**
 * Created by tluo on 1/13/17.
 */

public class BroadcastCheck {
    protected static final String dynACTION  = "com.android.broadcast.ANTI_DYN";
    protected static final String staticACTION = "com.android.broadcast.ANTI_STATIC";
    protected static final String dyn_stickyACTION = "com.android.broadcast.ANTI_DYN_STICKY";
    protected static final String static_stickyACTION = "com.android.broadcast.ANTI_STATIC_STICKY";

    protected String receiver_name = "com.panw.lab.antipluginsdk.AntiCheckCases.Dummy.DummyReceiver";

    DummyReceiver rhelper;

    public boolean isPlugin(Context ctx) {
        rhelper=new DummyReceiver(ctx);
        Log.v("Anti", "      [DummyReceiver] Register Action [" + dynACTION + "]");
        rhelper.registerAction(dynACTION);

        //startActivityFromService(ctx, IntentActivity.class);
        Thread t1 = new task_thread(ctx, this);
        t1.start();
        return false;
    }

    public void startActivityFromService(Context ctx, Class target){
        Intent dialogIntent = new Intent(ctx, target);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(dialogIntent);
    }

    public void checkEnabledComp(Context ctx) {
        Log.w("Anti", "+[Broadcast-Check] [checkEnabledComp] Begin");
        ctx.getPackageManager().setComponentEnabledSetting(
                new ComponentName(ctx, receiver_name),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Intent static_intent = new Intent(staticACTION);
        ctx.sendBroadcast(static_intent);

        SystemClock.sleep(5000);
        phoneback(ctx, "c2", "DummyReceiver-" + staticACTION, "1", "checkEnabledComp");
    }

    public void checkUnregisteredFilter(Context ctx) {
        Log.w("Anti", "+[Broadcast-Check] [checkUnregisteredFilter] Begin");
        rhelper.unregisterAction_all();
        Intent static_intent = new Intent(staticACTION);
        ctx.sendBroadcast(static_intent);

        SystemClock.sleep(5000);
        phoneback(ctx, "c2", "DummyReceiver-" + staticACTION, "1", "checkUnregisteredFilter");
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
        private BroadcastCheck mine;
        public task_thread(Context ctx, BroadcastCheck mine){
            this.ctx = ctx;
            this.mine = mine;
        }
        public void run() {
            try {
                mine.checkEnabledComp(ctx);
                sleep(2000);
                mine.checkUnregisteredFilter(ctx);
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}