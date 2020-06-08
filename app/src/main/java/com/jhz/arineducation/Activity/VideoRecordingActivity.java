package com.jhz.arineducation.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;
import com.jhz.arineducation.model.Keyword;
import com.jhz.arineducation.model.ModelLoader;
import com.jhz.arineducation.model.Pinyin;
import com.jhz.arineducation.model.TTS;
import com.jhz.arineducation.model.VideoRecorder;
import com.jhz.arineducation.model.WritingArFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecordingActivity extends AppCompatActivity implements ModelLoader.ModelLoaderCallbacks {

    private static final String TAG = VideoRecordingActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private WritingArFragment arFragment;
    private ModelRenderable andyRenderable;
    // Model loader class to avoid leaking the activity context.
    private ModelLoader modelLoader;

    // VideoRecorder encapsulates all the video recording functionality.
    private VideoRecorder videoRecorder;
    private ImageButton videoButton;

    private boolean mUserRequestedInstall = true;
    private TextView textView;
    private TextView textView1;
    private ImageButton imageButton;
    private Toolbar toolbar;
    private ImageButton button;
    private DBAdapter dbAdapter;
    private com.jhz.arineducation.model.TTS TTS;
    private Pinyin pinyin;
    private ImageButton snapButton;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_recording);
        arFragment = (WritingArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        videoButton=(ImageButton)findViewById(R.id.video);
        textView=(TextView)findViewById(R.id.textView);
        textView1=(TextView)findViewById(R.id.pinyin) ;
        imageButton=(ImageButton)findViewById(R.id.replay);
        button=(ImageButton)findViewById(R.id.button);
        dbAdapter=new DBAdapter(this);
        TTS=new TTS(this);
        pinyin=new Pinyin();
        snapButton=(ImageButton)findViewById(R.id.snap);
        linearLayout=(LinearLayout)findViewById(R.id.view);
        textView.setDrawingCacheEnabled(true);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        String str=intent.getStringExtra("data");
        String modelName=intent.getStringExtra("modelName");
        int language=intent.getIntExtra("language",-1);
        modelName=modelName+".sfb";
        TTS.setText(str);
        pinyin.setText(str);
        textView.setText(str);

        Toast.makeText(this,str+"++++++++++++++++++++++++++++++++++++",Toast.LENGTH_LONG);
        if (pinyin.isChi())
            textView1.setText(pinyin.getPinyin());

        Keyword keyword=new Keyword(str,this);

        if (dbAdapter.isExisting("Keyword","text",str)){
//            button.setText("已经收藏");
            button.setImageResource(R.drawable.bookmark_black_36x36);
        }else {
//            button.setText("添加收藏");
            button.setImageResource(R.drawable.bookmark_border_black_36x36);
        }

        ModelRenderable.builder()
//                .setSource(this, Uri.parse("cactus-big.sfb"))
                .setSource(this, Uri.parse(modelName))
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(andyRenderable);
                    andy.select();
                });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language==-1){
                    TTS.setLanguage();
                }else {
                    TTS.setLanguage(language);
                }
                if (!TTS.isEmpty()){
                    TTS.speak();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbAdapter.isExisting("Keyword","text",str)){
                    dbAdapter.delele(str);
//                    button.setText("添加收藏");
                    button.setImageResource(R.drawable.bookmark_border_black_36x36);
                }else {
                    dbAdapter.add(keyword);
//                    button.setText("已经收藏");
                    button.setImageResource(R.drawable.bookmark_black_36x36);
                }
            }
        });

        // Initialize the VideoRecorder.
        videoRecorder = new VideoRecorder();
        int orientation = getResources().getConfiguration().orientation;
        videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_2160P, orientation);
        videoRecorder.setSceneView(arFragment.getArSceneView());

        videoButton.setOnClickListener(this::toggleRecording);
        videoButton.setEnabled(true);
        videoButton.setImageResource(R.drawable.videocam_black_36x36);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check permission

        // check ARCore
        try {
            switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                case INSTALLED:
                    // Success, create the AR session.
                    break;
                case INSTALL_REQUESTED:
                    // Ensures next invocation of requestInstall() will either return
                    // INSTALLED or throw an exception.
                    mUserRequestedInstall = false;
                    return;
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                    .show();
            return;
        } catch (UnavailableDeviceNotCompatibleException e){
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                    .show();
            return;
        }
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
    protected void onPause() {
        if (videoRecorder.isRecording()) {
            toggleRecording(null);
        }
        super.onPause();
    }

    private void toggleRecording(View unusedView) {
        if (!arFragment.hasWritePermission()) {
            Log.e(TAG, "Video recording requires the WRITE_EXTERNAL_STORAGE permission");
            Toast.makeText(
                    this,
                    "Video recording requires the WRITE_EXTERNAL_STORAGE permission",
                    Toast.LENGTH_LONG)
                    .show();
            arFragment.launchPermissionSettings();
            return;
        }
        boolean recording = videoRecorder.onToggleRecord();
        if (recording) {
            videoButton.setImageResource(R.drawable.stop_black_36x36);
        } else {
            videoButton.setImageResource(R.drawable.videocam_black_36x36);
            String videoPath = videoRecorder.getVideoPath().getAbsolutePath();
            Toast.makeText(this, "Video saved: " + videoPath, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Video saved: " + videoPath);

            // Send  notification of updated content.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.TITLE, "Sceneform Video");
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, videoPath);
            getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    public void setRenderable(ModelRenderable modelRenderable) {

    }

    @Override
    public void onLoadException(Throwable throwable) {
        Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.e(TAG, "Unable to load andy renderable", throwable);
    }

    public Bitmap getBitmapFromView(View view){
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void viewSnapshot(View view){
//        view.setDrawingCacheEnabled(true);
////        获取缓存的 Bitmap
//        Bitmap drawingCache = view.getDrawingCache();
////        复制获取的 Bitmap
//        drawingCache = Bitmap.createBitmap(drawingCache);
////        关闭视图的缓存
//        view.setDrawingCacheEnabled(false);

//        Bitmap drawingCache = getBitmapFromView(view);
//        if (drawingCache != null) {
//            saveBitmap(drawingCache);
//            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
//        }

        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        if (bitmap != null) {
//            img_screen.setImageBitmap(bitmap);
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                String filePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(filePath);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();

                //发广播告诉相册有图片需要更新，这样可以在图册下看到保存的图片了
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                sendBroadcast(intent);

                Toast.makeText(this,"存储完成"+filePath+"sdCardPath"+sdCardPath,Toast.LENGTH_LONG);
            } catch (Exception e) {
            }
        }

    }

    private int saveBitmap(Bitmap bitmap){

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "保存失败,没有读写sd卡权限", Toast.LENGTH_SHORT).show();
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "ARinEducation";
        File appDir = new File(path , dirName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //文件名为时间
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(timeStamp));
        String fileName = sd + ".png";
        //获取文件
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            //通知系统相册刷新
            VideoRecordingActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(file.getPath()))));
            return 2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
