package mplugindemo.shengyuan.com.mplugin_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import mplugindemo.shengyuan.com.mplugin_base.MPluginActivity;

/**
 * Created by mapeng on 2018/8/19.
 */

public class MainActivity extends MPluginActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
//                .setTitle("这是标题")
//                .setMessage("有多个按钮")
//                .setIcon(R.mipmap.ic_launcher)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(MainActivity.this, "这是确定按钮", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .create();
//        alertDialog2.show();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }
}
