package com.panw.lab.antipluginsdk.AntiCheckCases;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Leverage Package Manager Service to check Plugin
 */

public class PackageChecker {
    // 因为Hook了PMS 所以从PM上面看不出区别
    // 能不能尝试用reflection来拿
    public void isPlugin(
            Context context,
            PackageManager pm) {
        String pkgName = context.getApplicationContext().getPackageName();
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //undeclaredPermissionCheck(context, pm);
        checkUIDProcess(context, am, pkgName);
        checkAppRuntimeDir(context, pm, pkgName);
        getCurrentProcessInfo3(context);
    }
    public boolean listAll(Context context,
                           PackageManager pm){
        String pkgname = context.getApplicationContext().getPackageName();
        Map<String, String> PkgInfo = new HashMap<String, String>();
        Map<String, String> AppInfo = new HashMap<String, String>();
        getCurrrentAppInfo(pm, pkgname);
        try {
            PackageInfo PI = pm.getPackageInfo(pkgname,
                    PackageManager.GET_CONFIGURATIONS |
                            PackageManager.GET_PERMISSIONS |
                            PackageManager.GET_ACTIVITIES |
                            PackageManager.GET_SERVICES|
                            PackageManager.GET_META_DATA
            );
            Log.i("Anti", "Get PackageInfo");
            PkgInfo.put("packageName", PI.packageName.toString());
            PkgInfo.put("sharedUserId", PI.sharedUserId);
            PkgInfo.put("versionName", PI.versionName.toString());
            if(PI.activities != null) {
                String activity_str = "";
                for (int i = 0; i < PI.activities.length; i++) {
                    activity_str += PI.activities[i].toString()+"\n";
                }
                PkgInfo.put("activities", activity_str);
            }
            if(PI.permissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.permissions.length; i++) {
                    perm_str += PI.permissions[i].toString()+"\n";
                }
                PkgInfo.put("permissions", perm_str);
            }
            if(PI.requestedPermissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.requestedPermissions.length; i++) {
                    perm_str += PI.requestedPermissions[i].toString()+"\n";
                }
                PkgInfo.put("requestedPermissions", perm_str);
            }
            if(PI.services != null) {
                String _str = "";
                for (int i = 0; i < PI.services.length; i++) {
                    _str += PI.services[i].toString()+"\n";
                }
                PkgInfo.put("services", _str);
            }
            for (Map.Entry<String, String> entry : PkgInfo.entrySet()) {
                Log.i("Anti", "  "+entry.getKey()+" : "+entry.getValue());
            }

            List<PackageInfo> apps;
            apps = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (Exception e) {
            Log.i("Anti", "Error To Get PackageInfo");
        }
        try {
            ApplicationInfo AI = pm.getApplicationInfo(pkgname, PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
            AppInfo.put("backupAgentName", AI.backupAgentName.toString());
            AppInfo.put("className", AI.className.toString());
            AppInfo.put("dataDir", AI.dataDir.toString());
            AppInfo.put("manageSpaceActivityName", AI.manageSpaceActivityName.toString());
            AppInfo.put("nativeLibraryDir", AI.nativeLibraryDir.toString());
            AppInfo.put("permission", AI.permission.toString());
            AppInfo.put("publicSourceDir", AI.publicSourceDir.toString());
            AppInfo.put("sourceDir", AI.sourceDir.toString());
            AppInfo.put("processName", AI.processName.toString());
            for (Map.Entry<String, String> entry : AppInfo.entrySet()) {
                Log.i("Anti", entry.getKey()+" : "+entry.getValue());
            }
        } catch (Exception e) {
            Log.i("Anti", "Error To Get ApplicationInfo");
        }
        return false;
    }

    private PackageInfo getCurrrentAppInfo(PackageManager pm, String pkgName){
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            if(applicationInfo.packageName.equals(pkgName)) {
                Log.i("Anti", "===");
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if(requestedPermissions != null) {
                        for (int i = 0; i < requestedPermissions.length; i++) {
                            Log.d("Anti", requestedPermissions[i]);
                        }
                    }
                }  catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    Log.i("Anti", "EEEE");
                }
            }
        }
        return null;
    }

    protected void checkAppRuntimeDir(Context context, PackageManager pm, String pkgName) {
        Log.w("Anti", "+[PKG-Check] [checkAppRuntimeDir] Begin");
        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
            boolean dataDir_wrong = !ai.dataDir.startsWith("/data/data/" + pkgName);
            boolean srcDir_wrong = !ai.sourceDir.startsWith("/data/app/" + pkgName);
            boolean pSrcDir_wrong = !ai.publicSourceDir.startsWith("/data/app/" + pkgName);

            phoneback(context, "result", "", ""+(dataDir_wrong | srcDir_wrong | pSrcDir_wrong), "checkAppRuntimeDir");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // DataDir: /data/data/clwang.chunyu.me.wcl_droid_plugin_demo/Plugin/com.panw.lab.antidemo/data/com.panw.lab.antidemo
        // SrcDir: /data/data/clwang.chunyu.me.wcl_droid_plugin_demo/Plugin/com.panw.lab.antidemo/apk/base-1.apk

        // DataDir: /data/data/com.panw.lab.antidemo
        // SrcDir: /data/app/com.panw.lab.antidemo-1/base.apk
    }
    protected void getCurrentProcessInfo3(Context context) {
        Log.d("AntiPlugin", "getCurrentProcessInfo2:getRunningTasks");
        Log.i("Anti", "\n\n=== getRunningTasks ===\n");
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String str = "";
        for (int i = 0; i < recentTasks.size(); i++)
        {
            str += "\n\tApplication executed : " +recentTasks.get(i).baseActivity.toShortString()+ "\t\t ID: "+recentTasks.get(i).id+"";
            Log.i("Anti", str);
            Log.d("AntiPlugin", str);
        }
    }
    protected void checkUIDProcess(Context context, ActivityManager am,  String pkgName){
        Log.w("Anti", "+[PKG-Check] [checkUIDProcess] Begin");
        int pid = android.os.Process.myPid();
        Log.i("Anti", "All Processes with the same UID");
        List<String> unknown_proc = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()){
            Log.i("Anti", "\t\tuid:("+appProcess.uid+")|pid("+appProcess.pid+")|Name("+appProcess.processName+")");
            if(!appProcess.processName.contains(pkgName)){
                unknown_proc.add(appProcess.uid+"_"+appProcess.pid+"_"+appProcess.processName);
            }
        }
        phoneback(context, "result", "", ""+(unknown_proc.size() > 0), "checkUIDProcess");
        //uid:(10069)|pid(18588)|Name(clwang.chunyu.me.wcl_droid_plugin_demo)
        //uid:(10069)|pid(18742)|Name(com.panw.lab.antidemo)
        //uid:(10069)|pid(18764)|Name(clwang.chunyu.me.wcl_droid_plugin_demo:PluginP02)

        //uid:(10058)|pid(16730)|Name(com.panw.lab.antidemo)
        //uid:(10058)|pid(16777)|Name(com.panw.lab.antidemo:remote)
    }

    private void undeclaredPermissionCheck(Context context, PackageManager pm){
        Log.w("Anti", "+[Package-Check] [undeclaredPermissionCheck] Begin");
        boolean found_undeclared = false;
        List<String> requestedPerms = getDeclaredPermissions(context, pm);
        List<String> allPerms = getAllPermissions(pm);
        allPerms.removeAll(requestedPerms);
        for (String perm : allPerms) {
            if(ContextCompat.checkSelfPermission(context, perm) == 0) {
                //Log.d("Anti", "     perm:" + perm);
                found_undeclared = true;
            }
        }
        phoneback(context, "result", "", ""+found_undeclared,"undeclaredPermissionCheck");
    }
    private List<String> getDeclaredPermissions(Context ctx, PackageManager pm){
        ArrayList<String> perms = new ArrayList<String>();
        String pkgname = ctx.getApplicationContext().getPackageName();
        try {
            PackageInfo PI = pm.getPackageInfo(pkgname,
                    PackageManager.GET_CONFIGURATIONS |
                            PackageManager.GET_PERMISSIONS |
                            PackageManager.GET_ACTIVITIES |
                            PackageManager.GET_SERVICES |
                            PackageManager.GET_META_DATA
            );
            if(PI.permissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.permissions.length; i++) {
                    perms.add(PI.permissions[i].toString());
                    perm_str += PI.permissions[i].toString()+"\n";
                }
                //Log.i("Anti", "permissions:"+perm_str);
            }
            if(PI.requestedPermissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.requestedPermissions.length; i++) {
                    perms.add(PI.requestedPermissions[i].toString());
                    perm_str += PI.requestedPermissions[i].toString()+"\n";
                }
                //Log.i("Anti", "requestedPermissions:" + perm_str);
            }
        } catch (Exception e) {
            Log.i("Anti", "Error To Get Declared Permissions of App");
        }
        return perms;
    }
    private List<String> getAllPermissions(PackageManager pm){
        List<String> perms = new ArrayList<String>();
        CharSequence csPermissionGroupLabel;
        CharSequence csPermissionLabel;
        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        for (PermissionGroupInfo pgi : lstGroups) {
            csPermissionGroupLabel = pgi.loadLabel(pm);
            try {
                List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                for (PermissionInfo pi : lstPermissions) {
                    csPermissionLabel = pi.loadLabel(pm);
                    //Log.e("Anti", "   " + pi.name + ": " + csPermissionLabel.toString());
                    perms.add(pi.name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return perms;
    }
    static public void phoneback(Context ctx, String key, String detail, String result, String task){
        Intent intent5 = new Intent("com.android.broadcast.RESULT");
        intent5.putExtra("key", key);
        intent5.putExtra("detail", detail);
        intent5.putExtra("result", result);
        intent5.putExtra("task", task);
        ctx.sendBroadcast(intent5);
    }
}
