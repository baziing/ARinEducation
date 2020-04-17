package com.jhz.arineducation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Locale;

public class TextActivity extends AppCompatActivity {

    private TextView textView;
    private TextToSpeech tts;
    private TextView textView1;
    private ImageButton imageButton;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        textView=(TextView)findViewById(R.id.textView);
        textView.setText("hellp");
        textView1=(TextView)findViewById(R.id.pinyin);
        imageButton=(ImageButton)findViewById(R.id.replay);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        textView.setText(intent.getStringExtra("data"));
        String str=intent.getStringExtra("data");

        Pinyin pinyin=new Pinyin(str);
        textView.setText(pinyin.getOutput());
//        char[] charstr=str.toCharArray();
//        String show="  ";
//        for (int i=0;i<charstr.length;i++){
//            show=show+charstr[i]+"  ";
//        }
//        textView.setText(show);


//        init();
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
        init();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                System.out.println("?????????????????");
            }
        });

        pinyin(intent.getStringExtra("data"));
    }

    private void init(){
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        tts.setPitch(1.0f);
        // 设置语速
        tts.setSpeechRate(0.5f);
    }

    private void pinyin(String string){
        ArrayList<String>arrayList=new ArrayList<String>();
        char[] c=string.toCharArray();
//        String[] pinyinArray= PinyinHelper.toHanyuPinyinStringArray(c[0]);
////        String[] pinyinArray= PinyinHelper.toHanyuPinyinStringArray('行');
//        for (int i=0;i<pinyinArray.length;++i){
//            System.out.println(pinyinArray[i]+"==============================");
//        }

        for (int i=0;i<c.length;i++){
            String[] pinyinArray= PinyinHelper.toHanyuPinyinStringArray(c[i]);
            arrayList.add(tone(c[i]));
        }

        //显示
        String pinyin="";
        for (int i=0;i<arrayList.size();i++){
            pinyin=pinyin+arrayList.get(i)+' ';
        }
        System.out.println(pinyin);
        textView1.setText(pinyin);

//        //输出拼音
//        HanyuPinyinOutputFormat hanyuPinyinOutputFormat=new HanyuPinyinOutputFormat();
//        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
//        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
//        pinyinArray = null;
//        try {
//            pinyinArray = PinyinHelper.toHanyuPinyinStringArray('行', hanyuPinyinOutputFormat);
//        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
//            badHanyuPinyinOutputFormatCombination.printStackTrace();
//        }
//        for (int i = 0; i < pinyinArray.length; ++i) {
//            System.out.println(pinyinArray[i]+"==============================");
//        }
    }

    private String tone(char c){
        String[] pinyinArray= PinyinHelper.toHanyuPinyinStringArray(c);
        HanyuPinyinOutputFormat hanyuPinyinOutputFormat=new HanyuPinyinOutputFormat();
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        pinyinArray = null;
        try {
            pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }
        for (int i = 0; i < pinyinArray.length; ++i) {
            System.out.println(pinyinArray[i]+"==============================");
        }
        return pinyinArray[0];
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
}
