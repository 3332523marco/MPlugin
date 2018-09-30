package mplugindemo.shengyuan.com.mplugin_core;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


/**
 * Created by mapeng on 2018/9/30.
 */

public class MPluginBaseActivity extends AppCompatActivity {

    private final static String TAG = MPluginBaseActivity.class.getSimpleName();
    protected Context pluginContext;
    private View mView;
    private MPluginManager mPluginManager;
    private boolean isPlugin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginManager = MPluginManager.getInstance(this);
        pluginContext = mPluginManager.getPluginPackgaeInfo().getContext();
        isPlugin = pluginContext != null;
        mPluginManager.addActCount();
    }

    @Override
    public void setContentView(int layoutResID) {
        if(isPlugin) {
            mView = LayoutInflater.from(pluginContext).inflate(layoutResID, null);
            super.setContentView(mView);
        }else{
            super.setContentView(layoutResID);
        }
    }

    @Override
    public View findViewById(int viewId) {
        if (mView == null) {
            return super.findViewById(viewId);
        } else {
            return mView.findViewById(viewId);
        }
    }

    protected Resources getPluginResources(){
        return pluginContext.getResources();
    }

    protected Context getPluginContext(){
        return !isPlugin?getBaseContext():pluginContext;
    }

    @Override
    public void finish() {
        if(isPlugin) {
            if (mPluginManager.isOnlyCount()) {
                Log.i(TAG, "MPluginBaseActivity finish ");
                Activity activity = (Activity) pluginContext;
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
            mPluginManager.delActCount();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pluginContext = null;
    }
}
