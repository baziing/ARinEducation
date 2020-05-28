package com.jhz.arineducation.model;

import org.litepal.crud.LitePalSupport;

public class Model extends LitePalSupport {
    private int type;
    private String chi;
    private String eng;
    private String modelName;
    private String pinyin;

    public Model(int type,String chi,String modelName,String pinyin,String eng){
        this.type=type;
        this.chi=chi;
        this.eng=eng;
        this.modelName=modelName;
        this.pinyin=pinyin;
    }

    public String getChi() {
        return chi;
    }

    public String getEng() {
        return eng;
    }

    public String getModelName() {
        return modelName;
    }

    public int getType() {
        return type;
    }

    public void setChi(String chi) {
        this.chi = chi;
    }

    public void setEng(String eng) {
        this.eng = eng;
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
