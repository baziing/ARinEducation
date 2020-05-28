package com.jhz.arineducation.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Locale;

public class TTS {

    private Context context;
    private String text;
    private TextToSpeech tts;

    public TTS(Context context){
        this.context=context;

        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // setLanguage设置语言
                    int result = tts.setLanguage(Locale.CHINA);
                    // TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失
                    // TextToSpeech.LANG_NOT_SUPPORTED：不支持
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    }
                }
            }
        });
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        tts.setPitch(1.0f);
        // 设置语速
        tts.setSpeechRate(0.5f);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLanguage(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("network_url",Context.MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");

        if(language.indexOf("chi_sim")!=-1){
            tts.setLanguage(Locale.CHINA);
        }else if (language.indexOf("eng")!=-1){
            tts.setLanguage(Locale.ENGLISH);
        }else {
            tts.setLanguage(Locale.CHINA);
        }
    }

    public void setLanguage(int language){
        switch (language){
            case 1:
                tts.setLanguage(Locale.CHINA);
                break;
            case 2:
                tts.setLanguage(Locale.ENGLISH);
                break;
            default:break;
        }
    }

    public boolean isEmpty(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("network_url",Context.MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");

        if (language.indexOf("eng")!=-1){
            String str=text;
            String string = "";
            if (str.equals("")) {
                return true;
            }
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLetter(ch)) {
                    string = string + ch;
                }
            }
            str=string;
            if (TextUtils.isEmpty(str))
                return true;
            else
                return false;
        }
        else {
            String str=text;
            String reg = "[^\u4e00-\u9fa5]";
            str = str.replaceAll(reg, "");
            if (TextUtils.isEmpty(str))
                return true;
            else
                return false;
        }
    }

    public boolean isEmpty(int language){
        if (language==2){
            String str=text;
            String string = "";
            if (str.equals("")) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLetter(ch)) {
                    string = string + ch;
                }
            }
            str=string;
            if (TextUtils.isEmpty(str))
                return false;
            else
                return true;
        }
        else if (language==1){
            String str=text;
            String reg = "[^\u4e00-\u9fa5]";
            str = str.replaceAll(reg, "");
            if (TextUtils.isEmpty(str))
                return false;
            else
                return true;
        }
        return false;
    }

    public boolean speak(){
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
        System.out.println(text);
        return true;
    }

}
