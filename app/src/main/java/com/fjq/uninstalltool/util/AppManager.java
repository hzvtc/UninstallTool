package com.fjq.uninstalltool.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fjq.uninstalltool.model.App;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class AppManager {
    private static final String TAG = "AppManager";
    private static AppManager mInstance;
    private Context context;

    private AppManager(Context context) {
        this.context = context;
    }

    public static AppManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppManager(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * 获取用户手机上已经安装的非系统自带APP列表
     */
    public List<App> getInstalledApps() {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        Log.d(TAG, "getInstalledApps: " + packages.size());
        List<App> listMap = new ArrayList(packages.size());
        for (int j = 0; j < packages.size(); j++) {

            PackageInfo packageInfo = packages.get(j);
            // 显示非系统软件
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = packageInfo.packageName;
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager()).getCurrent();
                App app = new App();
                app.setAppIcon(appIcon);
                app.setAppName(appName);
                app.setPackageName(packageName);
                listMap.add(app);
            }
        }
        return listMap;
    }

    /**
     * APK静默卸载
     *
     * @param packageName 需要卸载应用的包名
     * @return true 静默卸载成功 false 静默卸载失败
     */
    public boolean uninstall(String packageName) {
        String result = apkProcess("pm uninstall " + packageName + "\n");
        Log.e(TAG, "uninstall log:" + result);
        if (result != null
                && (result.endsWith("Success") || result.endsWith("Success\n"))) {
            return true;
        }
        return false;
    }

    /**
     * 应用安装、卸载处理
     *
     * @param args 安装、卸载参数
     * @return Apk安装、卸载结果
     */
    public String apkProcess(String command) {
        DataOutputStream dataOutputStream = null;
        InputStream errIs = null;  //error stream
        InputStream inIs = null;   // process stream
        ByteArrayOutputStream resultStream = null;
        String result = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            //等待任务完成
            process.waitFor();
            int read = -1;

            resultStream = new ByteArrayOutputStream();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                resultStream.write(read);
            }

            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                resultStream.write(read);
            }

            byte[] data = resultStream.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (resultStream != null) {
                    resultStream.close();
                }
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
