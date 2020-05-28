package com.jhz.arineducation;

public class Language {

    int language_id;
    String language_name;

    Language(int language_id,String language_name){
        this.language_id=language_id;
        this.language_name=language_name;
    }

    public int getLanguage_id() {
        return language_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_id(int language_id) {
        this.language_id = language_id;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }
}
