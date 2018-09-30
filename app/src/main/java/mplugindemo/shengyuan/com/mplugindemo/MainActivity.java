package mplugindemo.shengyuan.com.mplugindemo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import mplugindemo.shengyuan.com.mplugin_core.MPluginManager;
import mplugindemo.shengyuan.com.mplugin_core.proxy.ProxyActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String  dexPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "mplugin168.apk";
    private MPluginManager mPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPluginManager = MPluginManager.getInstance(this);
        mPluginManager.installPlugin(dexPath);
        findViewById(R.id.openapk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPluginManager.isInstall()){
                    startActivity(new Intent(MainActivity.this, ProxyActivity.class));
                }else{
                    Toast.makeText(MainActivity.this,"开启失败",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
//        mPluginManager.destroy();
    }
}
