package mplugindemo.shengyuan.com.mplugin_core.resource;

import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MPluginResource {
    public Resources resources;
    public AssetManager assetManager;
    public Resources.Theme theme;

    public MPluginResource(Resources resources, AssetManager assetManager, Resources.Theme theme) {
        this.resources = resources;
        this.assetManager = assetManager;
        this.theme = theme;
    }
}
