package mplugindemo.shengyuan.com.mplugin_core.proxy;


import android.os.Bundle;
import android.view.View;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;

/**
 * Created by mapeng on 2018/9/30.
 */

public class ProxyActivity extends ProxyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        setPluginResource(mPluginManager.getPluginPackgaeInfo().getPluginResource());
        mPluginManager.startPluginActivity(this, getIntent().getStringExtra("className"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MPluginManager.getInstance(this).destroy();
    }
}
