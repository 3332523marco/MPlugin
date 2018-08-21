package mplugindemo.shengyuan.com.mplugin_demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mapeng on 2018/8/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private List<String> itemList;
    private LayoutInflater inflater;

    public RecyclerAdapter(Context context, List<String> itemList) {
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String item = itemList.get(position);
        holder.title.setText(item);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击事件
                if(itemClickListener!=null)
                    itemClickListener.OnItemClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return null == itemList ? 0 : itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public View rootView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.title);
            rootView = itemView;
        }
    }

    public OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }

    public interface OnItemClickListener{
        void OnItemClick(View view,int position);
    }
}
