package com.jhz.arineducation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 100;

    //    权限
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> permissionList = new ArrayList<>();

    private Button button;
    private String mDataPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/tesseract/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button)findViewById(R.id.go);

        //判断是否有权限
        if (!checkPermissions()){
            showDialog();
        }

        //判断是否有文件夹
        checkDir();

        //下载资源
        downloadTrainedData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setClass(MainActivity.this,SelectPictureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkDir(){
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/ARinEducation";
        File file=new File(path);
        if (!file.exists()){
            file.mkdir();
            System.out.println("创建成功");
        }else
            System.out.println("已经存在");
    }

    private void downloadTrainedData(){

    }

    private Boolean checkPermissions(){
        for (int i = 0; i < permissions.length; i++) {
//            添加还未授予的权限
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("缺少权限");
                return false;
            }
        }
        System.out.println("权限通过");
        return true;
    }

    //    确认权限并且申请
    private void getPermissions(){
        permissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
//            添加还未授予的权限
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() > 0) {
//            有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }else{
//            说明权限都已经通过，可以做你想做的事情去
            System.out.println("申请权限完成");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限申请成功
                //do something
                System.out.println("做需要使用到权限的事情");
            } else {
                //权限申请失败
                //5.)用户点了拒绝权限，判断是否选择了不在提示
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    System.out.println("提示用户，禁止了权限，没有勾选不再提示框");
                } else {
                    //可以在这里提示用户，根据需求跳转到权限设置页面让用户手动授权或者取消授权
                    System.out.println("提示用户，禁止了权限，并且勾选了不在提示框");
                }
            }
        }
    }

    private void showDialog(){
        AlertDialog dialog=new AlertDialog.Builder(this).setMessage("使用该软件需要一些权限，是否去设置？")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPermissions();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }
}
