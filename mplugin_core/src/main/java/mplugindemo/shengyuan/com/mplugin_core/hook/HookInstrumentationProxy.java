package mplugindemo.shengyuan.com.mplugin_core.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

import mplugindemo.shengyuan.com.mplugin_core.proxy.ProxyActivity;


/**
 * Created by mapeng on 2018/5/30.
 */

public class HookInstrumentationProxy extends Instrumentation {

    private PackageManager mPackageManager;
    private Instrumentation mInstrumentation;
    private Context mContext;
    public static final String REAL_INTENT  = "realIntent";
    public static final String TAG = HookInstrumentationProxy.class.getSimpleName();

    public HookInstrumentationProxy(Context context, PackageManager packageManager,
                                    Instrumentation instrumentation){
        mPackageManager = packageManager;
        mInstrumentation = instrumentation;
        mContext = context;
    }

    //Activity 到 AMS 的过程
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        Intent intentProxy = intent;
        Log.i(TAG,"HookInstrumentationProxy execStartActivity");
        //清单中找不到当前的Activity，表示未注册，需要替换为代理的Activity
        if (resolveInfos != null && resolveInfos.size() == 0){
            intentProxy = new Intent();
            intentProxy.setComponent(new ComponentName(mContext, ProxyActivity.class));
            intentProxy.putExtra(REAL_INTENT, intent);
            Log.i(TAG,"intentProxy  replace intent");
        }
        try {
            Class<?> instrumentationClass = Class.forName("android.app.Instrumentation");
            Method execStartActivityMethod = instrumentationClass.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            execStartActivityMethod.setAccessible(true);
            ActivityResult activityResult = (ActivityResult) execStartActivityMethod.invoke(mInstrumentation, who, contextThread, token, target,
                    intentProxy, requestCode, options);
            Log.i(TAG,"instrumentation execStartActivityMethod");
            return activityResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //ActivityThread到handler启动Activity过程
    @Override
    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        //取出替换过的Activity
        if (intent.getParcelableExtra(REAL_INTENT) != null){
            intent = intent.getParcelableExtra(REAL_INTENT);
            className = intent.getComponent().getClassName();
        }
        return (Activity)cl.loadClass(className).newInstance();
    }

}
