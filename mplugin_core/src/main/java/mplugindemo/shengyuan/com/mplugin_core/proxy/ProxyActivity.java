package mplugindemo.shengyuan.com.mplugin_core.proxy;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;

/**
 * Created by mapeng on 2018/9/30.
 */

public class ProxyActivity extends ProxyBaseActivity{
    private static final String  dexPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "mplugin168.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        setPluginResource(MPluginManager.getInstance(this).getPluginPackgaeInfo().getPluginResource());
        MPluginManager.getInstance(this).startActivity(this,"mplugindemo.shengyuan.com.mplugin_demo.MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MPluginManager.getInstance(this).destroy();
    }
}
