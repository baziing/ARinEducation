package com.jhz.arineducation.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class Keyword {

    String text;
    int language_id;

    public Keyword(){}

    public Keyword(String text, Context context){
        this.text=text;
        SharedPreferences sharedPreferences=context.getSharedPreferences("network_url",Context.MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if(language.indexOf("chi_sim")!=-1){
            this.language_id=1;
        }else if (language.indexOf("eng")!=-1){
            this.language_id=2;
        }else {
            this.language_id=1;
        }
    }

    public int getLanguage_id() {
        return language_id;
    }

    public String getText() {
        return text;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
