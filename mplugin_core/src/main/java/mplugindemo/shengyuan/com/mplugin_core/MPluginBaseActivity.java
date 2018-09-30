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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPluginManager = MPluginManager.getInstance(this);
        pluginContext = mPluginManager.getPluginPackgaeInfo().getContext();
        mPluginManager.addActCount();
    }

    @Override
    public void setContentView(int layoutResID) {
        if(pluginContext!=null) {
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
        return pluginContext==null?getBaseContext():pluginContext;
    }

    @Override
    public void finish() {
        if(mPluginManager.isOnlyCount()){
            Log.i(TAG,"MPluginBaseActivity finish ");
            Activity activity = (Activity)pluginContext;
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        mPluginManager.delActCount();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pluginContext = null;
    }
}
