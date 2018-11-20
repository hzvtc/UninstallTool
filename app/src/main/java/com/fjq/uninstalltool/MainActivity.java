package com.fjq.uninstalltool;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fjq.uninstalltool.model.App;
import com.fjq.uninstalltool.util.AppManager;
import com.fjq.uninstalltool.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.preBtn)
    Button preBtn;
    @BindView(R.id.nextBtn)
    Button nextBtn;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.search)
    Button search;
    private ProgressDialog mDefaultDialog;
    @BindView(R.id.appList)
    RecyclerView appList;
    @BindView(R.id.deleteBtn)
    Button deleteBtn;

    private void hideDialog() {
        if (mDefaultDialog != null && mDefaultDialog.isShowing()) {
            mDefaultDialog.hide();
        }
    }

    private AppAdapter appAdapter;
    private List<App> apps;
    private Map<Integer, List<App>> pageAppMap;
    private int curIndex;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    /**
     * 显示app列表
     */
    class showAppListTask extends AsyncTask<String, Void, List<App>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            editText.setText("");
            showDialog("正在查找应用");
        }

        @Override
        protected List<App> doInBackground(String... voids) {
            apps = AppManager.
                    getInstance(MainActivity.this).getInstalledApps();
            List<App> searchApps = new ArrayList<>();
            for (App app : apps) {
                if (app.getAppName().contains(voids[0])) {
                    searchApps.add(app);
                }
            }
//            initPageAppMap();
            return searchApps;
        }

        @Override
        protected void onPostExecute(List<App> apps) {
            super.onPostExecute(apps);
            initAppView(apps);
            hideDialog();
        }
    }

    private void initPageAppMap() {
        if (pageAppMap == null) {
            pageAppMap = new HashMap<>();
        }
        if (apps != null && apps.size() > 0) {
            int size = apps.size();
            boolean temp = size % 8 == 0;
            index = temp ? size / 8 : (size / 8) + 1;
            curIndex = 1;
            for (int i = 1; i <= index; i++) {
                int end = i * 8;
                if (i == index && !temp) {
                    end = apps.size();
                }
                pageAppMap.put(i, apps.subList((i - 1) * 8, end));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }

    private void initAppView(List<App> apps) {
        if (apps != null && apps.size() > 0) {
            deleteBtn.setEnabled(true);
        }
        else {
            deleteBtn.setEnabled(false);
        }
        if (appAdapter == null) {
            appAdapter = new AppAdapter(MainActivity.this, apps,
                    R.layout.item_app);
            appList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            appList.setAdapter(appAdapter);
        } else {
            appAdapter.refreshList(apps);
        }
    }

    /**
     * 批量卸载app程序任务
     */
    class uninstallApkTask extends AsyncTask<Void, Integer, Void> {
        public static final String TAG = "uninstallApkTask";
        private int size;
        private List<App> selectedList;
        private List<App> successCount;
        private List<App> failCount;

        @Override
        protected void onPreExecute() {
            successCount = new ArrayList<>();
            failCount = new ArrayList<>();
            selectedList = appAdapter.getmDatas();
            if (selectedList != null && selectedList.size() > 0) {
                size = selectedList.size();
                showDialog("正在卸载(" + 1 + "/" + size + ")");
                super.onPreExecute();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int result = values[0];
            int curIndex = values[1];
            if (result == 0) {
                successCount.add(selectedList.get(curIndex));
            } else {
                failCount.add(selectedList.get(curIndex));
            }
            curIndex++;
            showDialog("正在卸载(" + (curIndex + 1) + "/" + size + ")");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < size; i++) {
                App app = selectedList.get(i);
                boolean result = AppManager.getInstance(MainActivity.this).uninstall(app.
                        getPackageName());
                int arg = result ? 0 : 1;
                int index = i;
                publishProgress(arg, index);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new showAppListTask().execute(text);
            hideDialog();
            Toast.makeText(MainActivity.this, "成功卸载：" + successCount.size() + "个" +
                    "卸载失败：" + failCount.size() + "个", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog(String message) {
        if (mDefaultDialog == null) {
            mDefaultDialog = new ProgressDialog(this);
            mDefaultDialog.setCanceledOnTouchOutside(false);//默认true
        }
        mDefaultDialog.setMessage(message);
        mDefaultDialog.show();
    }
    private  String text;
    @OnClick({R.id.deleteBtn, R.id.preBtn, R.id.nextBtn, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search:
                text = editText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    new showAppListTask().execute(text);
                } else {
                    Toast.makeText(MainActivity.this,
                            "请输入应用名", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.deleteBtn:
                new uninstallApkTask().execute();
                break;
            case R.id.preBtn:
                if (index > 1) {
                    if (curIndex == 1) {
                        curIndex = index;
                    } else {
                        curIndex--;
                    }
                    initAppView(pageAppMap.get(curIndex));
                }

                break;
            case R.id.nextBtn:
                if (index > 1) {
                    if (curIndex == index) {
                        curIndex = 1;
                    } else {
                        curIndex++;
                    }
                    initAppView(pageAppMap.get(curIndex));
                }
                break;
        }

    }
}
