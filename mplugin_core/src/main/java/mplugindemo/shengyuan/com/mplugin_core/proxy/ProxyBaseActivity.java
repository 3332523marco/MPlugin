package mplugindemo.shengyuan.com.mplugin_core.proxy;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import mplugindemo.shengyuan.com.mplugin_core.resource.MPluginResource;

/**
 * Created by mapeng on 2018/9/30.
 */

public class ProxyBaseActivity extends AppCompatActivity {
    private MPluginResource mPluginResource;

    public void setPluginResource(MPluginResource mPluginResource) {
        this.mPluginResource = mPluginResource;
    }

    @Override
    public synchronized AssetManager getAssets() {
        return mPluginResource == null ? super.getAssets() : mPluginResource.assetManager;
    }

    @Override
    public synchronized Resources getResources() {
        return mPluginResource == null ? super.getResources() : mPluginResource.resources;
    }

    @Override
    public synchronized Resources.Theme getTheme() {
        return mPluginResource == null ? super.getTheme() : mPluginResource.theme;
    }

}
