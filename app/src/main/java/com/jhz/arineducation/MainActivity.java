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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 100;

    //    权限
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
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


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Intent intent=new Intent();
//                intent.setClass(MainActivity.this,SelectPictureActivity.class);
//                startActivity(intent);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584802701000&di=c24e57b0b9b721c0def1894a6f8ed6c8&imgtype=0&src=http%3A%2F%2Fbbs.jooyoo.net%2Fattachment%2FMon_0905%2F24_65548_2835f8eaa933ff6.jpg")
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i("myTag", "下载失败");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        writeFile(response);
                    }
                });
            }
        });
    }

    private void writeFile(Response response) {
        InputStream is = null;
        FileOutputStream fos = null;
        is = response.body().byteStream();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ARinEducation/";
        long fileSize1 = response.body().contentLength();
        System.out.println("大小");
        System.out.println(fileSize1/1024);
        String fileName="1.jpg";
        File file = new File(path, fileName);
        long total = response.body().contentLength();
        int len = 0;
        byte[] buf = new byte[2048];
        try {
            fos = new FileOutputStream(file);
            fos = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
//                LogUtil.e(TAG,"download progress : " + progress);
//                mView.onDownloading("",progress);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("myTag", "下载成功");
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

    private File getFile(){
        String root = Environment.getExternalStorageDirectory().getPath();
        File file = new File(root,"updateDemo.apk");
        return file;
    }

    private long getFileStart(){
        String root = Environment.getExternalStorageDirectory().getPath();
        File file = new File(root,"updateDemo.apk");
        return file.length();
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
