package mplugindemo.shengyuan.com.mplugin_base;

import android.content.Context;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MPluginContextManager {

    private static MPluginContextManager INSTANCE;
    private Context context;

    public static MPluginContextManager getInstance() {
        if (INSTANCE == null) {
            synchronized (MPluginContextManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MPluginContextManager();
                }
            }
        }
        return INSTANCE;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
