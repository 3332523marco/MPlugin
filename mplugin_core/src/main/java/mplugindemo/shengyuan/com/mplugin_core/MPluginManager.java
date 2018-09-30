package mplugindemo.shengyuan.com.mplugin_core;

import android.content.Context;
import android.content.Intent;

import mplugindemo.shengyuan.com.mplugin_core.hook.HookUtil;
import mplugindemo.shengyuan.com.mplugin_core.resource.MPluginResourceManager;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MPluginManager {

    private static MPluginManager INSTANCE;
    private MPluginPackgaeInfo mPluginPackgaeInfo;
    private Context mContext;
    private int actCount;//统计当前启动插件的页面数量
    private boolean mIsInstall;

    public static MPluginManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MPluginManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MPluginManager(context);
                }
                return INSTANCE;
            }
        }
        return INSTANCE;
    }

    public MPluginManager(Context context) {
       this.mContext = context;
        mPluginPackgaeInfo = new MPluginPackgaeInfo();
    }

    public void prepare(){
        HookUtil.hookInstrumentation(mContext);
    }

    public void installPlugin(String dexPath){
        mPluginPackgaeInfo.setPluginResource(MPluginResourceManager.getInstance().loadResources(dexPath,mContext));
        mPluginPackgaeInfo.setPluginLoader(HookUtil.hookDexElements(mContext,dexPath));
        if(mPluginPackgaeInfo.getPluginLoader()!=null && mPluginPackgaeInfo.getPluginResource()!=null){
            mIsInstall = true;
        }
    }

    public MPluginPackgaeInfo getPluginPackgaeInfo(){
        return mPluginPackgaeInfo;
    }

    public void startActivity(Context context,String className){
        try {
            mPluginPackgaeInfo.setContext(context);
            context.startActivity(new Intent(context,mPluginPackgaeInfo.getPluginLoader().loadClass(className)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInstall(){
        return mIsInstall;
    }

    public boolean isOnlyCount() {
        return actCount == 1;
    }

    public void addActCount() {
        this.actCount++;
    }

    public void delActCount(){
        this.actCount--;
    }

    public void destroy(){
        mPluginPackgaeInfo.setContext(null);
    }
}
