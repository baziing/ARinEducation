package com.jhz.arineducation;

import org.litepal.crud.LitePalSupport;

public class Model extends LitePalSupport {
    private int type;
    private String characters;
    private String modelName;
    private String pinyin;

    Model(int type,String characters,String modelName,String pinyin){
        this.type=type;
        this.characters=characters;
        this.modelName=modelName;
        this.pinyin=pinyin;
    }

    public String getCharacters() {
        return characters;
    }

    public String getModelName() {
        return modelName;
    }

    public int getType() {
        return type;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        return pinyin;
    }
}
