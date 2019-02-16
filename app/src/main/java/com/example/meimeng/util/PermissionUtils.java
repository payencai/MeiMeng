package com.example.meimeng.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.example.meimeng.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wrt on 2017/10/26.
 */

public class PermissionUtils {
    private static ApplyPermission applyPermission;

    public PermissionUtils(){
        super();
    }

    public static void needPermission(Object object, String[] permissions, int requestCode){
        requestPermission(object,permissions,requestCode);
    }
    public static void needPermission(Object object, String permission, int requestCode){
        needPermission(object,new String[]{permission},requestCode);
    }

    /**
     * 请求权限
     * @param object
     * @param permissions
     * @param requestCode
     */
    private static void requestPermission(Object object, String[] permissions, int requestCode){
        //版本号需要大于23
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            Toast.makeText(getActivity(object),"版本号低于23,无需动态申请权限!", Toast.LENGTH_SHORT).show();
            applyPermission.doSuccess();
            return;
        }
        List<String> denyPermissions=checkPermissons(getActivity(object),permissions);
        //请求权限的方法
        if(denyPermissions.size()>0){
            if(object instanceof Activity ||object instanceof Fragment){
                getActivity(object).requestPermissions(denyPermissions.toArray((new String[denyPermissions.size()]))
                        ,requestCode);
            }else{
                throw new IllegalArgumentException(object.getClass().getName()+"is not support");
            }
        }else{
            applyPermission.doSuccess();
        }
    }
    /**
     * 检查权限存在不存在
     * @param activity
     * @param permissions
     * @return
     */
    private static List<String> checkPermissons(Activity activity, String[] permissions){
        List<String> denyPermissions=new ArrayList<>();
        for(String value:permissions){
            if(ContextCompat.checkSelfPermission(activity,value)!= PackageManager.PERMISSION_GRANTED){

                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 申请权限回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(Object object, int requestCode
            , String[] permissions, int[] grantResults){
        List<String> deniedPermissions=new ArrayList<>();

        for(int i=0;i<grantResults.length;i++){
            //当用户拒绝过这个权限，提示用户，为什么需要这个权限-----小米对于该方法不使用，一直返回false
            boolean isTip= ActivityCompat.shouldShowRequestPermissionRationale(getActivity(object),permissions[i]);
            if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                if(!isTip){//标明用户已经彻底禁止弹出权限请求了
                    doAgain(getActivity(object));
                }
                deniedPermissions.add(permissions[i]);
            }
        }
        if(deniedPermissions.size()>0){
            applyPermission.doFailed();
        }else{
            applyPermission.doSuccess();
        }
    }


    /**
     * 将传入的界面都转换成Activity
     * @param object
     * @return
     */
    private static Activity getActivity(Object object){
        if(object instanceof Fragment){
            return ((Fragment)object).getActivity();
        }else if(object instanceof Activity){
            return (Activity) object;
        }
        return null;
    }

    /**
     * doSuccess()--申请权限成功
     * doFailed()--失败回调的方法
     */
    public static interface ApplyPermission{
        void doFailed();
        void doSuccess();
    }
    public static void setApplyPermission(ApplyPermission mApplyPermission){
        applyPermission=mApplyPermission;
    }

    /**
     * 当第一次拒绝后，让用户去设置界面去设置
     * @param activity
     */
    private static void doAgain(final Activity activity){
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog,null);
        final Dialog dialog=new Dialog(activity);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSystem()){
                    getMiuiSettingIntent(activity);
                }else{
                    activity.startActivity(getAppDetialSettingIntent(activity));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 大多数的手机跳转设置权限的界面
     * @param activity
     * @return
     */
    private static Intent getAppDetialSettingIntent(Activity activity){
        Intent localIntent=new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=9){
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",activity.getPackageName(),null));
        }else if(Build.VERSION.SDK_INT<=8){
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settions","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName",activity.getPackageName());
        }
        return localIntent;
    }

    /**
     * 对小米手机单独处理
     */
    /**
     * 判断手机是小米手机
     */
    public static final String SYS_MIUI = "sys_miui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static boolean getSystem(){
        boolean flag=false;
        try {
            Properties prop= new Properties();
            prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
            if(prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null){
                flag=true;
            }
        }catch (IOException e){
            e.printStackTrace();
            return flag;
        }
        return flag;

    }
    private static void getMiuiSettingIntent(Activity activity){
        try {
            //MIUI 8
            Intent localIntent=new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", activity.getPackageName());
            activity.startActivity(localIntent);
        }catch (Exception e){
            try {
                //MIUI 5/6/7
                Intent localIntent=new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", activity.getPackageName());
                activity.startActivity(localIntent);
            }catch (Exception e1){
                //否则调转到应用详情
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                // 根据包名打开对应的设置界面
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }
        }

    }

}