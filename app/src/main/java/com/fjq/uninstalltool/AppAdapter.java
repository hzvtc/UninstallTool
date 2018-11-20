package com.fjq.uninstalltool;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;

import com.fjq.uninstalltool.model.App;
import com.fjq.uninstalltool.recylerview.CommonAdapter;
import com.fjq.uninstalltool.recylerview.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解决RecylerView缓存机制导致的CheckBox选中混乱的问题
 * SharedPreferences保存选中状态
 */
public class AppAdapter extends CommonAdapter<App> {
    public static final String TAG = "AppAdapter";
    public AppAdapter(Context mContext, List<App> mDatas, int mItemLayoutId) {
        super(mContext, mDatas, mItemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, final App item, int position) {
        item.setPosition(position);
        helper.setCheckBoxListener(R.id.checkBox, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                Log.d(TAG, "onCheckedChanged: "+item.getPosition());
            }
        });
        helper.setCheckBox(R.id.checkBox,item.isChecked());
        helper.setImageResource(R.id.appIcon,item.getAppIcon());
        helper.setText(R.id.appName,item.getAppName());
        helper.setText(R.id.packageName,item.getPackageName());
    }

    public List<App> getSelectedList(){
        List<App> selectedList = new ArrayList<>();
        for (App app:mDatas){
            if (app.isChecked()){
                selectedList.add(app);
            }
        }
        return selectedList;
    }
}
