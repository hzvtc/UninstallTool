package com.fjq.uninstalltool.recylerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fjq.uninstalltool.App;
import com.fjq.uninstalltool.R;


public class ViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private View itemView;
    public ViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.mViews = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if (view==null){
            view = itemView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text)
    {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId)
    {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawable
     * @return
     */
    public ViewHolder setImageResource(int viewId, Drawable drawable)
    {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawable
     * @return
     */
    public ViewHolder setImageResource(int viewId, Drawable acualImg,int errImg)
    {
        ImageView view = getView(viewId);
        Glide.with(App.context).load(acualImg).error(errImg).into(view);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm)
    {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为View设置透明度
     * @param viewId
     * @param value
     * @return
     */
    public ViewHolder setAlpha(int viewId,float value){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            getView(viewId).setAlpha(value);
        } else
        {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * 切换文字正常和选中的样式
     * @param viewId
     * @param selected
     * @return
     */
    public ViewHolder setTextState(int viewId,boolean selected){
        TextView textView = getView(viewId);
        textView.setSelected(selected);
        return this;
    }

    /**
     * 切换图片正常和选中的样式
     * @param viewId
     * @param selected
     * @return
     */
    public ViewHolder setImageState(int viewId,boolean selected){
        ImageView imageView = getView(viewId);
        imageView.setSelected(selected);
        return this;
    }

    /**
     * 设置checkbox的选中状态
     * @param viewId
     * @param selected
     * @return
     */
    public ViewHolder setCheckBox(int viewId,boolean selected){
        CheckBox checkBox = getView(viewId);
        checkBox.setSelected(selected);
        return this;
    }

    public ViewHolder setCheckBoxListener(int viewId, CompoundButton.
            OnCheckedChangeListener listener){
        CheckBox checkBox = getView(viewId);
        checkBox.setOnCheckedChangeListener(listener);
        return this;
    }
}
