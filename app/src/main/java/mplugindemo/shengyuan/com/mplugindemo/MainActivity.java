package mplugindemo.shengyuan.com.mplugindemo;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String  dexPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "mplugin_demo28.apk";
    private MPluginManager mPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPluginManager = MPluginManager.getInstance(this);
        setPluginResource(mPluginManager.loadPluginResource(dexPath));
        mPluginManager.loadApk(dexPath,"mplugindemo.shengyuan.com.mplugin_demo.MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        mPluginManager.destroy();
    }
}
