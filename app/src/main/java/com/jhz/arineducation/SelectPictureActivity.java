package com.jhz.arineducation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.osgi.OpenCVNativeLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectPictureActivity extends AppCompatActivity {

//    常量
    private String mDataPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ARinEducation/";
    private String p="/storage/self/primary/tessdata/";
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_CAMERA=3;

    private Bitmap bitmap;//获取测试图片
    private Uri imageUri;
    private String result;
    private String text;


//    UI元件
    private Button cameraButton;
    private Button albumButton;
    private Button textButton;
    private AutoCompleteTextView autoCompleteTextView;
    private Button deleteButton;
    private RadioGroup radioGroup;
    private RadioButton chiButton;
    private RadioButton engButton;
//    private Button backButton;
//    private Button ocrButton;
//    private TextView textView;//输出结果
//    private ImageView imageView;
//    private Button speakButton;

    private TextToSpeech tts;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test);
        // 参数Context,TextToSpeech.OnInitListener
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
            /*
                使用的是小米手机进行测试，打开设置，在系统和设备列表项中找到更多设置，
            点击进入更多设置，在点击进入语言和输入法，见语言项列表，点击文字转语音（TTS）输出，
            首选引擎项有三项为Pico TTs，科大讯飞语音引擎3.0，度秘语音引擎3.0。其中Pico TTS不支持
            中文语言状态。其他两项支持中文。选择科大讯飞语音引擎3.0。进行测试。

                如果自己的测试机里面没有可以读取中文的引擎，
            那么不要紧，我在该Module包中放了一个科大讯飞语音引擎3.0.apk，将该引擎进行安装后，进入到
            系统设置中，找到文字转语音（TTS）输出，将引擎修改为科大讯飞语音引擎3.0即可。重新启动测试
            Demo即可体验到文字转中文语言。
             */
                    // setLanguage设置语言
                    int result = tts.setLanguage(Locale.CHINA);
                    // TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失
                    // TextToSpeech.LANG_NOT_SUPPORTED：不支持
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"数据丢失或不支持",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

//        初始化
        cameraButton=(Button)findViewById(R.id.camera);
        albumButton=(Button)findViewById(R.id.album);
        textButton=(Button)findViewById(R.id.search);
        deleteButton=(Button)findViewById(R.id.delete);
        autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.input);
        radioGroup=(RadioGroup)findViewById(R.id.group);
        chiButton=(RadioButton)findViewById(R.id.chi);
        engButton=(RadioButton)findViewById(R.id.eng);
//        backButton=(Button)findViewById(R.id.back);
//        ocrButton=(Button)findViewById(R.id.ocr);
//        textView=(TextView) findViewById(R.id.result);
//        imageView=(ImageView)findViewById(R.id.img);
//        speakButton=(Button)findViewById(R.id.speak);

        radioGroup.setOnCheckedChangeListener(new MyRadioButtonListener());

        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if(language.indexOf("chi_sim")!=-1){
            chiButton.setChecked(true);
        }else if (language.indexOf("eng")!=-1){
            engButton.setChecked(true);
        }else {
            chiButton.setChecked(true);
        }

        initAutoComplete("history",autoCompleteTextView);

        List<String> permissionList = new ArrayList<>();

        init();

        //搜索
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                saveHistory("history",autoCompleteTextView);

                String text=autoCompleteTextView.getText().toString();

                text=checkString(text);
                System.out.println(text+"+++++++++++_____________________");
                if (!TextUtils.isEmpty(text))
                {
                    SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
                    String language=sharedPreferences.getString("language","");
                    if (language.indexOf("eng")!=-1){
                        System.out.println("eng+++++++++++++++++++++++++++++++");
                        DBHelper dbHelper=new DBHelper();
                        if (dbHelper.findObjectByEng(text)!=null){//在数据库中存在
                            Intent intent=new Intent();
                            intent.putExtra("data",text);
                            intent.putExtra("modelName",dbHelper.findObjectByEng(text));
                            intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                            startActivity(intent);
                        }else {//在数据库中不存在
                            Intent intent=new Intent();
                            intent.putExtra("data",text);
                            intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                            startActivity(intent);
                        }
                    }else {
                        System.out.println("chi+++++++++++++++++++++++++++++++=====");
                        System.out.println(text+"+++++++++++++++++++++++++++++++=====");
                        DBHelper dbHelper=new DBHelper();
                        if (dbHelper.findObjectByChi(text)!=null){//在数据库中存在
                            System.out.println(dbHelper.findObjectByChi(text)+"_____________");
                            Intent intent=new Intent();
                            intent.putExtra("data",text);
                            intent.putExtra("modelName",dbHelper.findObjectByChi(text));
                            intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                            startActivity(intent);
                        }else {//在数据库中不存在
                            Intent intent=new Intent();
                            intent.putExtra("data",text);
                            intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                            startActivity(intent);
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"请输入正确的格式",Toast.LENGTH_LONG).show();

                }
//                DBHelper dbHelper=new DBHelper();
//                if (dbHelper.findobject(text)!=null){//在数据库中存在
//                    Intent intent=new Intent();
//                    intent.putExtra("data",text);
//                    intent.putExtra("modelName",dbHelper.findobject(text));
//                    intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                    startActivity(intent);
//                }else {//在数据库中不存在
//                    Intent intent=new Intent();
//                    intent.putExtra("data",text);
//                    intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                    startActivity(intent);
//                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHistory("history",autoCompleteTextView);
            }
        });

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

////        测试ocr
//        ocrButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                checkOCR();
//                pinyin("haha");
//            }
//        });
//
////        speak
//        speakButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                init();
//            }
//        });
//
////        返回
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void initAutoComplete(String field,AutoCompleteTextView autoCompleteTextView){
        SharedPreferences sharedPreferences=getSharedPreferences("network_url", 0);
        String longhistory = sharedPreferences.getString("history", "");
        String[]  hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);

        if(hisArrays.length > 50){
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setDropDownHeight(350);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setCompletionHint("最近的5条记录");
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }

    private void saveHistory(String field,AutoCompleteTextView autoCompleteTextView){
        String text=autoCompleteTextView.getText().toString();
        SharedPreferences sharedPreferences=getSharedPreferences("network_url", 0);
        String longhistory = sharedPreferences.getString(field, "");

        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            System.out.println(text);
            sb.insert(0, text + ",");
            sharedPreferences.edit().putString("history", sb.toString()).commit();
        }

        initAutoComplete("history",autoCompleteTextView);
    }

    private void deleteHistory(String field,AutoCompleteTextView autoCompleteTextView){
        SharedPreferences sharedPreferences=getSharedPreferences("network_url", 0);
        sharedPreferences.edit().clear().commit();
        initAutoComplete("history",autoCompleteTextView);
    }

    private void init(){
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        tts.setPitch(1.0f);
        // 设置语速
        tts.setSpeechRate(0.5f);
    }


    private Boolean checkPicByte(Bitmap bitmap){
        if (bitmap.getAllocationByteCount()>128*100)
            return false;
        return true;
    }

    private Bitmap compressPic(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        int options=100;
        while (byteArrayOutputStream.toByteArray().length/1024>256){
            Toast.makeText(getApplicationContext(),String.valueOf(byteArrayOutputStream.toByteArray().length/1024), Toast.LENGTH_LONG).show();
            System.out.println(String.valueOf(byteArrayOutputStream.toByteArray().length/1024));
            byteArrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,options,byteArrayOutputStream);
            options=options-10;
        }
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        bitmap=BitmapFactory.decodeStream(byteArrayInputStream,null,null);
        return bitmap;
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

        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if(language.indexOf("chi_sim")!=-1){
            mTess.init(mDataPath, "chi_sim");
        }else if (language.indexOf("eng")!=-1){
            mTess.init(mDataPath, "eng");
        }else {
            mTess.init(mDataPath, "chi_sim");
        }

        mTess.init(mDataPath, "chi_sim");//mFilePath不知道？

//        mTess.setImage(bitmap);
        Bitmap bitmap=BitmapFactory.decodeResource(this.getResources(),R.drawable.tes);
        mTess.setImage(bitmap);

        String OCRresult = mTess.getUTF8Text(); // 拿到字符串结果
        result=OCRresult;
//        textView.setText(OCRresult);
//        mTess.init("/storage/self/primary/", "chi_sim");//mFilePath不知道？
    }

    private void pinyin(String string){
        String[] pinyinArray= PinyinHelper.toHanyuPinyinStringArray('行');
        for (int i=0;i<pinyinArray.length;++i){
            System.out.println(pinyinArray[i]+"==============================");
        }

        //输出拼音
        HanyuPinyinOutputFormat hanyuPinyinOutputFormat=new HanyuPinyinOutputFormat();
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        pinyinArray = null;
        try {
            pinyinArray = PinyinHelper.toHanyuPinyinStringArray('行', hanyuPinyinOutputFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        for (int i = 0; i < pinyinArray.length; ++i) {
            System.out.println(pinyinArray[i]+"==============================");
        }
    }

    private void checkOCR(Bitmap bitmap){
        String lang = "chi_sim+eng";//中文简体+英文
        TessBaseAPI mTess = new TessBaseAPI();
        String path = Environment.getExternalStorageDirectory().getPath();
//        Toast.makeText(getApplicationContext(),"/tesseract/",Toast.LENGTH_LONG).show();
//        mTess.init(mDataPath, "chi_sim");//mFilePath不知道？

        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if(language.indexOf("chi_sim")!=-1){
            mTess.init(mDataPath, "chi_sim");
        }else if (language.indexOf("eng")!=-1){
            mTess.init(mDataPath, "eng");
        }else {
            mTess.init(mDataPath, "chi_sim");
        }

        mTess.setImage(bitmap);
//        Bitmap bitmap=BitmapFactory.decodeResource(this.getResources(),R.drawable.test);
//        mTess.setImage(bitmap);

        String OCRresult = mTess.getUTF8Text(); // 拿到字符串结果
        result=OCRresult;
        OCRresult=checkString(OCRresult);
//        textView.setText(OCRresult);
        checkString(OCRresult);
        tts.speak(OCRresult, TextToSpeech.QUEUE_FLUSH, null);
//        mTess.init("/storage/self/primary/", "chi_sim");//mFilePath不知道？
        text=OCRresult;
    }

    private String checkString(String str){
        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if (language.indexOf("eng")!=-1){
            String string = "";
            if (str.equals("")) {
                return "";
            }
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLetter(ch)) {
                    string = string + ch;
                }
            }
            str=string;
        }else {
            String reg = "[^\u4e00-\u9fa5]";
            str = str.replaceAll(reg, "");
            System.out.println(str);
        }
//        String reg = "[^\u4e00-\u9fa5]";
//        str = str.replaceAll(reg, "");
//        System.out.println(str);
        return str;
    }

    //处理图片
    private Bitmap cvPic(Bitmap bitmap){
        OpenCVNativeLoader openCVNativeLoader=new OpenCVNativeLoader();
        openCVNativeLoader.init();
        Mat mat=new Mat();
        Mat resultMat=new Mat();
        Utils.bitmapToMat(bitmap,mat);

//        Imgproc.cvtColor(mat,resultMat,Imgproc.COLOR_BGR2GRAY);//灰度化
//        Imgproc.threshold(resultMat,resultMat,100,255,Imgproc.THRESH_BINARY);//二值化
////        Mat erodeElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(26,26));//腐蚀，填充
////        Imgproc.erode(resultMat,resultMat,erodeElement);
//        List<MatOfPoint>contours=new ArrayList<>();//轮廓检测
//        Imgproc.findContours(resultMat,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
//        Imgproc.drawContours(resultMat,contours,-1, new Scalar(0,255,0),4);

        Imgproc.cvtColor(mat, resultMat, Imgproc.COLOR_RGB2GRAY);//灰度化
        Imgproc.blur(resultMat, resultMat, new Size(3, 3));//低通滤波处理
//        Imgproc.Canny(resultMat, resultMat, 50, 100);//边缘检测处理类
        Imgproc.threshold(resultMat, resultMat, 165, 255, Imgproc.THRESH_BINARY);//二值化
        Imgproc.medianBlur(resultMat, resultMat, 3);//中值平滑处理
        Mat element_9 = new Mat(20, 20, 0, new Scalar(1));
        Imgproc.morphologyEx(resultMat, element_9, Imgproc.MORPH_CROSS, element_9);//闭运算
//        List<MatOfPoint>contours=new ArrayList<>();//轮廓检测
//        Imgproc.findContours(resultMat,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
//        Imgproc.drawContours(resultMat,contours,-1, new Scalar(0,255,0),1);
        
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
//                        imageView.setImageBitmap(bitmap);// 将其设置到一个ImageView中显示
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
                        compressPic(bitmap);
                        bitmap=cvPic(bitmap);
//                        imageView.setImageBitmap(bitmap);
                        checkOCR(bitmap);

                        if (!TextUtils.isEmpty(text))
                        {
                            SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
                            String language=sharedPreferences.getString("language","");
                            if (language.indexOf("eng")!=-1){
                                DBHelper dbHelper=new DBHelper();
                                if (dbHelper.findObjectByEng(text)!=null){//在数据库中存在
                                    Intent intent=new Intent();
                                    intent.putExtra("data",text);
                                    intent.putExtra("modelName",dbHelper.findObjectByEng(text));
                                    intent.setClass(SelectPictureActivity.this,ARActivity.class);
                                    startActivity(intent);
                                }else {//在数据库中不存在
                                    Intent intent=new Intent();
                                    intent.putExtra("data",text);
                                    intent.setClass(SelectPictureActivity.this,TextActivity.class);
                                    startActivity(intent);
                                }
                            }else {
                                DBHelper dbHelper=new DBHelper();
                                if (dbHelper.findObjectByChi(text)!=null){//在数据库中存在
                                    Intent intent=new Intent();
                                    intent.putExtra("data",text);
                                    intent.putExtra("modelName",dbHelper.findObjectByChi(text));
                                    intent.setClass(SelectPictureActivity.this,ARActivity.class);
                                    startActivity(intent);
                                }else {//在数据库中不存在
                                    Intent intent=new Intent();
                                    intent.putExtra("data",text);
                                    intent.setClass(SelectPictureActivity.this,TextActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"请输入正确的格式",Toast.LENGTH_LONG).show();

                        }

//                        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
//                        String language=sharedPreferences.getString("language","");
//                        if (language.indexOf("eng")!=-1){
//                            DBHelper dbHelper=new DBHelper();
//                            if (dbHelper.findObjectByEng(text)!=null){//在数据库中存在
//                                Intent intent=new Intent();
//                                intent.putExtra("data",text);
//                                intent.putExtra("modelName",dbHelper.findObjectByEng(text));
//                                intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                                startActivity(intent);
//                            }else {//在数据库中不存在
//                                Intent intent=new Intent();
//                                intent.putExtra("data",text);
//                                intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                                startActivity(intent);
//                            }
//                        }else {
//                            DBHelper dbHelper=new DBHelper();
//                            if (dbHelper.findObjectByChi(text)!=null){//在数据库中存在
//                                Intent intent=new Intent();
//                                intent.putExtra("data",text);
//                                intent.putExtra("modelName",dbHelper.findObjectByChi(text));
//                                intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                                startActivity(intent);
//                            }else {//在数据库中不存在
//                                Intent intent=new Intent();
//                                intent.putExtra("data",text);
//                                intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                                startActivity(intent);
//                            }
//                        }

//                        DBHelper dbHelper=new DBHelper();
//                        if (dbHelper.findobject(text)!=null){//在数据库中存在
//                            Intent intent=new Intent();
//                            intent.putExtra("data",text);
//                            intent.putExtra("modelName",dbHelper.findobject(text));
//                            intent.setClass(SelectPictureActivity.this,ARActivity.class);
//                            startActivity(intent);
//                        }else {//在数据库中不存在
//                            Intent intent=new Intent();
//                            intent.putExtra("data",text);
//                            intent.setClass(SelectPictureActivity.this,TextActivity.class);
//                            startActivity(intent);
//                        }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
                default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tts.stop();
        tts.shutdown();
    }

    @Override
    protected void onDestroy() {
        if (tts!=null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
        super.onDestroy();
    }

    class MyRadioButtonListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.chi:
                    setSharedPreference("language","chi_sim");
                    System.out.println("chi");
                    break;
                case R.id.eng:
                    setSharedPreference("language","eng");
                    System.out.println("eng");
                    break;
            }
        }
    }

    public void setSharedPreference(String data,String str){
        SharedPreferences.Editor editor=getSharedPreferences("network_url",MODE_PRIVATE).edit();
        editor.putString(data,str);
        editor.apply();
    }

}
