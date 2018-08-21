package mplugindemo.shengyuan.com.mplugindemo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MPluginManager.getInstance(base).prepare();
    }
}