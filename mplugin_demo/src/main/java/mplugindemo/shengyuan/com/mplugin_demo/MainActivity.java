package mplugindemo.shengyuan.com.mplugin_demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import mplugindemo.shengyuan.com.mplugin_core.MPluginBaseActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MainActivity extends MPluginBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // 步骤1:加载生成的so库文件
    // 注意要跟so库文件名相同
    static {
        System.loadLibrary("hello_jni");
    }

    // 步骤2:定义在JNI中实现的方法
    public native String getFromJNI();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(getPluginResources().getString(R.string.app_name))
                .setMessage("有多个按钮")
                .setIcon(getPluginResources().getDrawable(R.mipmap.ic_launcher_roun22d))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "这是确定按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
                alertDialog.show();
            }
        });
        findViewById(R.id.btn_requestnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Request request=new Request.Builder().url("https://github.com/").build();
                //get
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        try {
                            OkHttpClient okHttpClient=new OkHttpClient();
                            Response response=okHttpClient.newCall(request).execute();
                            if (response.isSuccessful()){
                                String body=response.body().string();
                                String headers=response.headers().toString();
                                Log.i(TAG, "response: "+response.toString());
                            }else {
                                Log.i(TAG, "response fail: "+response.code()+response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        findViewById(R.id.btn_jni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,getFromJNI(),Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }
}
