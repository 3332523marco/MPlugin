package mplugindemo.shengyuan.com.mplugindemo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.squareup.leakcanary.LeakCanary;

import java.io.File;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MainApplication extends Application {

    private static final String  apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "mplugin168.apk";

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MPluginManager.getInstance(base).prepare(apkPath,true);
    }
}