package mplugindemo.shengyuan.com.mplugin_core;

import android.content.Context;
import android.content.Intent;

import dalvik.system.DexClassLoader;
import mplugindemo.shengyuan.com.mplugin_base.MPluginContextManager;
import mplugindemo.shengyuan.com.mplugin_core.hook.HookUtil;
import mplugindemo.shengyuan.com.mplugin_core.resource.MPluginResource;
import mplugindemo.shengyuan.com.mplugin_core.resource.ResourceManager;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MPluginManager {

    private static MPluginManager INSTANCE;
    private Context mContext;

    public static MPluginManager getInstance(Context context) {
        if (INSTANCE == null && context.getApplicationContext()!=null) {
            synchronized (MPluginManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MPluginManager(context);
                }
                return INSTANCE;
            }
        }else{
            return new MPluginManager(context);
        }
    }

    public MPluginManager(Context context) {
       this.mContext = context;
    }

    public void prepare(){
        HookUtil.hookInstrumentation(mContext);
    }

    public MPluginResource loadPluginResource(String dexPath){
       return ResourceManager.getInstance().loadResources(dexPath,mContext);
    }

    public void installPlugin(String dexPath,String className){
        HookUtil.hookDexElements(mContext,dexPath);
        startActivity(dexPath,className);
    }

    public void destroy(){
        INSTANCE = null;
        mContext = null;
        MPluginContextManager.getInstance().setContext(null);
    }

    private void startActivity(String dexPath,String className){
        Class<?> clazz = null;
        try {
            DexClassLoader loader = new DexClassLoader(dexPath, mContext.getCacheDir().getAbsolutePath(),null, mContext.getClassLoader());
            clazz = loader.loadClass(className);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mContext.startActivity(new Intent(mContext,clazz));
    }
}
