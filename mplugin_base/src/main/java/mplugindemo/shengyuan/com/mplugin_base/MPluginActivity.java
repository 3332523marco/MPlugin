package mplugindemo.shengyuan.com.mplugin_base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MPluginActivity extends AppCompatActivity{

    protected Context pluginContext;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pluginContext = MPluginContextManager.getInstance().getContext();
    }

    @Override
    public void setContentView(int layoutResID) {
        mView = LayoutInflater.from(pluginContext).inflate(layoutResID, null);
        super.setContentView(mView);
    }

    @Override
    public View findViewById(int viewId) {
        if (mView == null) {
            return null;
        } else {
            return mView.findViewById(viewId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pluginContext = null;
    }
}
