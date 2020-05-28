package com.jhz.arineducation.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jhz.arineducation.DB.DBHelper;
import com.jhz.arineducation.R;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 100;


    //    权限
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.CAMERA};
    List<String> permissionList = new ArrayList<>();

    private Button button;
    private String mDataPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/tesseract/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);

        button=(Button)findViewById(R.id.go);

        //判断是否有权限
        if (!checkPermissions()){
            showDialog();
        }

        //判断是否是第一次启动
        checkFirstRun();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,SelectPictureActivity.class);
                startActivity(intent);
            }
        });
    }

    //判断是否是第一次启动
    private void checkFirstRun(){
        SharedPreferences sharedPreferences=getSharedPreferences("FirstRun",0);
        Boolean firstRun = sharedPreferences.getBoolean("First",true);
        if (firstRun){
            sharedPreferences.edit().putBoolean("First",false).commit();
            System.out.println("第一次启动");
            dataInitialize();
        }else{
            System.out.println("不是第一次启动");
        }
        DBHelper dbHelper=new DBHelper();
        dbHelper.initialize();
    }

    //初始化
    private void dataInitialize(){
//        Connector.getDatabase();
        DBHelper dbHelper=new DBHelper();
        dbHelper.initialize();

        //判断是否有文件夹
        checkDir("ARinEducation");
        checkDir("ARinEducation/tessdata");

        //判断是否有data文件
        checkData();

        //初始化语言设置
        SharedPreferences.Editor editor=getSharedPreferences("network_url",MODE_PRIVATE).edit();
        editor.putString("language","chi_sim");
        editor.apply();

    }

    private void checkData(){
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/ARinEducation/tessdata/";
        String[] datas=new String[]{"eng.traineddata","chi_sim.traineddata"};
        for (int i=0;i<datas.length;i++){
            File file=new File(path+datas[i]);
            if (!file.exists()){
                System.out.println("不存在"+datas[i]);
                copy(datas[i]);
            }else {
                System.out.println(datas[i]+"已经存在");
            }
        }
    }

    private void copy(String dataName){
        Toast.makeText(this, "1dsafa", Toast.LENGTH_LONG).show();
        System.out.println("==============从a");

        try {
            System.out.println("==============从asset复制文件到内存==============copyAssets============================.");
            String newPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ARinEducation/tessdata/";
            File files = new File(newPath);
            String fileName=dataName;
            File file = new File(newPath, fileName);

            InputStream is = null;

            try {
                AssetManager manager = getAssets();
                if (manager == null) return;
                is = manager.open(dataName);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (is == null) return;
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
            System.out.println("==============从asset复制文件到内存==============copyAssets  success============================.");
        }catch (Exception e){
            System.out.println("==============从asset复制文件到内存==============copyAssets  error============================.");
            e.printStackTrace();
        }

    }

    private void checkDir(String filename){
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+filename;
        File file=new File(path);
        if (!file.exists()){
            file.mkdir();
            System.out.println("创建成功");
            Toast.makeText(this, "1"+path, Toast.LENGTH_LONG).show();
        }else {
            System.out.println("已经存在");
        }
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
