package com.gwm.carapitest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwm.carapitest.APITest.CarTestResult;

import java.util.List;

public class ResultListAdapter extends BaseAdapter {

    private Context mContext;//上下文对象
    private List<CarTestResult> mResultList;//ListView显示的数据

    public ResultListAdapter(Context context, List<CarTestResult> resultList) {
        this.mContext = context;
        this.mResultList = resultList;
    }

    public void setBindData(List<CarTestResult> resultList) {
        this.mResultList = resultList;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position%2 == 0) {
            convertView.setBackgroundColor(mContext.getColor(R.color.evenline_color));
        } else {
            convertView.setBackgroundColor(mContext.getColor(R.color.oddline_color));
        }
        viewHolder.image.setImageResource(mResultList.get(position).isPassed() ? R.drawable.ok : R.drawable.error);
        viewHolder.pathText.setText(mResultList.get(position).getPath());
        viewHolder.functionText.setText(mResultList.get(position).getMethod());
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private final class ViewHolder {


        TextView pathText;
        TextView functionText;
        ImageView image;

        ViewHolder(View view) {
            pathText = view.findViewById(R.id.tv_path);
            functionText =  view.findViewById(R.id.tv_function);
            image = view.findViewById(R.id.image);
        }
    }
}