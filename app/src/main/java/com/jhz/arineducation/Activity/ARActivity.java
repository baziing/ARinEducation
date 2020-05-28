package com.jhz.arineducation.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;
import com.jhz.arineducation.model.Keyword;
import com.jhz.arineducation.model.Pinyin;

import java.util.Locale;

public class ARActivity extends AppCompatActivity {

//    private static final String TAG = HelloSceneformActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private boolean mUserRequestedInstall = true;
    private TextView textView;
    private TextView textView1;
    private ImageButton imageButton;
    private TextToSpeech tts;
    private Toolbar toolbar;
    private Button button;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        textView=(TextView)findViewById(R.id.textView);
        textView1=(TextView)findViewById(R.id.pinyin) ;
        imageButton=(ImageButton)findViewById(R.id.replay);
        button=(Button)findViewById(R.id.button);
        dbAdapter=new DBAdapter(this);

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
        modelName=modelName+".sfb";

        Keyword keyword=new Keyword(str,this);

        if (dbAdapter.isExisting("Keyword","text",str)){
            button.setText("已经收藏");
        }else {
            button.setText("添加收藏");
        }

        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if (language.indexOf("eng")!=-1){
            textView.setText(str);
        }else{
            Pinyin pinyin=new Pinyin(str);
            textView.setText(pinyin.getOutput());
            textView1.setText(pinyin.getTone());
        }
//        Pinyin pinyin=new Pinyin(str);
//        textView.setText(pinyin.getOutput());
//        textView1.setText(pinyin.getTone());

//        SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
//        String language=sharedPreferences.getString("language","");
//        if(language.indexOf("chi_sim")!=-1){
//            textView1.setText(pinyin.getTone());
//        }else if (language.indexOf("eng")!=-1){
//        }else {
//            textView1.setText(pinyin.getTone());
//        }

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
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

        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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
                    int result;
                    SharedPreferences sharedPreferences=getSharedPreferences("network_url",MODE_PRIVATE);
                    String language=sharedPreferences.getString("language","");
                    if(language.indexOf("chi_sim")!=-1){
                        result = tts.setLanguage(Locale.CHINA);
                    }else if (language.indexOf("eng")!=-1){
                        result = tts.setLanguage(Locale.ENGLISH);
                    }else {
                        result = tts.setLanguage(Locale.CHINA);
                    }

//                    int result = tts.setLanguage(Locale.CHINA);
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
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        tts.setPitch(1.0f);
        // 设置语速
        tts.setSpeechRate(0.5f);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbAdapter.isExisting("Keyword","text",str)){
                    dbAdapter.delele(str);
                    button.setText("添加收藏");
                }else {
                    dbAdapter.add(keyword);
                    button.setText("已经收藏");
                }
            }
        });

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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
