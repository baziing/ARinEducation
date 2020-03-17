package com.jhz.arineducation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVNativeLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectPictureActivity extends AppCompatActivity {

//    常量
    private String mDataPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/tesseract/";
    private String p="/storage/self/primary/tessdata/";
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_CAMERA=3;

    private Bitmap bitmap;//获取测试图片
    private Uri imageUri;
    private String result;

//    UI元件
    private Button cameraButton;
    private Button albumButton;
    private Button backButton;
    private Button ocrButton;
    private TextView textView;//输出结果
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);

//        初始化
        cameraButton=(Button)findViewById(R.id.camera);
        albumButton=(Button)findViewById(R.id.album);
        backButton=(Button)findViewById(R.id.back);
        ocrButton=(Button)findViewById(R.id.ocr);
        textView=(TextView) findViewById(R.id.result);
        imageView=(ImageView)findViewById(R.id.img);

        List<String> permissionList = new ArrayList<>();

//        开启摄像机
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });

//        打开相册
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic();
            }
        });

//        测试ocr
        ocrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOCR();
            }
        });

//        返回
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(SelectPictureActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void takePic(){
        File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        imageUri = FileProvider.getUriForFile(SelectPictureActivity.this, "com.jhz.cameraalbumtest.fileprovider", outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    private void selectPic(){
        //intent可以应用于广播和发起意图，其中属性有：ComponentName,action,data等
        Intent intent=new Intent();
        intent.setType("image/*");
        //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
        //类型的内容给你选择
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void cropPic(Uri uri){
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void checkOCR(){
        String lang = "chi_sim+eng";//中文简体+英文
        TessBaseAPI mTess = new TessBaseAPI();
        String path = Environment.getExternalStorageDirectory().getPath();
//        Toast.makeText(getApplicationContext(),"/tesseract/",Toast.LENGTH_LONG).show();
        mTess.init("/storage/self/primary/", "chi_sim");//mFilePath不知道？
        mTess.setImage(bitmap);
        String OCRresult = mTess.getUTF8Text(); // 拿到字符串结果
        result=OCRresult;
        textView.setText(OCRresult);
    }

    //处理图片
    private Bitmap cvPic(Bitmap bitmap){
        OpenCVNativeLoader openCVNativeLoader=new OpenCVNativeLoader();
        openCVNativeLoader.init();
        Mat mat=new Mat();
        Mat resultMat=new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Imgproc.cvtColor(mat,resultMat,Imgproc.COLOR_BGR2GRAY);//灰度化
        Imgproc.threshold(resultMat,resultMat,100,255,Imgproc.THRESH_BINARY);//二值化
        Mat erodeElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(26,26));//腐蚀，填充
        Imgproc.erode(resultMat,resultMat,erodeElement);
        List<MatOfPoint>contours=new ArrayList<>();//轮廓检测
        Imgproc.findContours(resultMat,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(resultMat,contours,-1, new Scalar(0,255,0),4);
        Utils.matToBitmap(resultMat,bitmap);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode){
            case TAKE_PHOTO:
                cropPic(imageUri);
                break;
            case CHOOSE_PHOTO:
                Uri uri = data.getData();
                cropPic(uri);
                break;
            case CROP_CAMERA:
                if (resultCode==RESULT_OK){
                    try {
                        // 调用BitmapFactory的decodeStream()方法将imageUri保存中的这张照片解析成Bitmap对象
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);// 将其设置到一个ImageView中显示
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode==RESULT_OK){
                    Uri resultUri = result.getUri();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        //获取图片
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(resultUri));
                        bitmap=cvPic(bitmap);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(),e);
                    }
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
