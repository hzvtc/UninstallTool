package com.fjq.uninstalltool.recylerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fjq.uninstalltool.model.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 1.点击同一个item不处理
 * @param <T>
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "CommonAdapter";
    protected Context mContext;
    protected List<T> mDatas;
    protected int mItemLayoutId;
    public CommonAdapter(Context mContext, List<T> mDatas, int mItemLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mItemLayoutId = mItemLayoutId;
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        position = position%mDatas.size();
        convert(holder,mDatas.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mDatas!=null&&mDatas.size()>0){
            return mDatas.size();
        }
        return 0;
    }

    public abstract void convert(ViewHolder helper, T item,int position);
    //刷新列表
    public void refreshList(List<T> goodsList) {
        this.mDatas = goodsList;
        notifyDataSetChanged();
    }
    //添加单个刷新
    public void addrefreshList(T item) {
        this.mDatas.add(item);
        int position = mDatas.size()-1;
        notifyItemInserted(position);
    }
    //删除单个刷新
    public void deleteRefreshList(int position,T item){
        Iterator<T> iterator = mDatas.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (t==item) {
                iterator.remove();
            }
        }
        deleteRefreshList(position);
    }

    public void deleteRefreshList(int position) {
        notifyItemRemoved(position);
    }

    /**
     * 下拉刷新 上拉加载
     * @param goodsList
     * @param type 0 代表下拉刷新 1 代表上拉加载
     */
    public void addrefreshList(List<T> goodsList,int type) {
        int position;
        if (type==0){
            position = 0;
        }
        else {
            position = this.mDatas.size();
        }
        this.mDatas.addAll(position,goodsList);
        notifyItemRangeChanged(position,goodsList.size());
    }

    //刷新item
    public void refreshItem(int position){
        notifyItemChanged(position);
    }
}
