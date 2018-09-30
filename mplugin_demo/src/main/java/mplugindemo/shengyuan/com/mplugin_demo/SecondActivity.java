package mplugindemo.shengyuan.com.mplugin_demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mplugindemo.shengyuan.com.mplugin_core.MPluginBaseActivity;

/**
 * Created by mapeng on 2018/8/19.
 */

public class SecondActivity extends MPluginBaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 初始化適配器
        mRecyclerAdapter = new RecyclerAdapter(getPluginContext(), getData());
        mRecyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Toast.makeText(SecondActivity.this.getApplicationContext(),"item click "+position,Toast.LENGTH_SHORT).show();
            }
        });
        // 设置适配器
        mRecyclerView.setAdapter(mRecyclerAdapter);
     }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < 50 ; i++){
            list.add("title"+i);
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeAllViews();
        mRecyclerView = null;
    }
}
