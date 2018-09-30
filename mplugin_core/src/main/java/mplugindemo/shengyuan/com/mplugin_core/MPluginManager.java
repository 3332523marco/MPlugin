package mplugindemo.shengyuan.com.mplugin_core;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;

import mplugindemo.shengyuan.com.mplugin_core.hook.HookUtil;
import mplugindemo.shengyuan.com.mplugin_core.proxy.ProxyActivity;
import mplugindemo.shengyuan.com.mplugin_core.resource.MPluginResourceManager;
import mplugindemo.shengyuan.com.mplugin_core.util.FileUtils;

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

    /**
     * 安装插件apk
     * @param apkPath apk路径
     * @param isCopySo 是否拷贝插件so库 若插件中存在so，需要拷贝到宿主中才能正常读取
     */
    public void prepare(String apkPath,boolean isCopySo){
        if(TextUtils.isEmpty(apkPath)){
            return;
        }
        mPluginPackgaeInfo.setApkPath(apkPath);
        mPluginPackgaeInfo.setPluginResource(MPluginResourceManager.getInstance().loadResources(apkPath,mContext));
        mPluginPackgaeInfo.setPluginLoader(HookUtil.hookApkInfo(mContext,apkPath,isCopySo));
        if(mPluginPackgaeInfo.getPluginLoader()!=null && mPluginPackgaeInfo.getPluginResource()!=null){
            mIsInstall = true;
        }
    }

    public void startPluginActivity(Context context,String className){
        Intent intent = new Intent(context, ProxyActivity.class);
        intent.putExtra("className",className);
        context.startActivity(intent);
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
