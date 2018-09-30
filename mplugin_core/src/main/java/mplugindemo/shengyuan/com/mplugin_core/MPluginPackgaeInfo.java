package mplugindemo.shengyuan.com.mplugin_core;

import android.content.Context;

import mplugindemo.shengyuan.com.mplugin_core.resource.MPluginResource;

/**
 * Created by mapeng on 2018/9/30.
 */

public class MPluginPackgaeInfo {
    private MPluginResource mPluginResource;
    private ClassLoader mPluginLoader;
    private Context mContext;

    public MPluginResource getPluginResource() {
        return mPluginResource;
    }

    public void setPluginResource(MPluginResource pluginResource) {
        this.mPluginResource = pluginResource;
    }

    public ClassLoader getPluginLoader() {
        return mPluginLoader;
    }

    public void setPluginLoader(ClassLoader pluginLoader) {
        this.mPluginLoader = pluginLoader;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public void clear(){
        mPluginLoader = null;
        mContext = null;
        mPluginResource = null;
    }
}
