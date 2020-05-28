package com.jhz.arineducation;

public class ModelName {

    int type;
    String chi;
    String eng;
    String modelName;

    ModelName(int type,String chi,String eng,String modelName){
        this.type=type;
        this.chi=chi;
        this.eng=eng;
        this.modelName=modelName;
    }

    public String getEng() {
        return eng;
    }

    public int getType() {
        return type;
    }

    public String getModelName() {
        return modelName;
    }

    public String getChi() {
        return chi;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setChi(String chi) {
        this.chi = chi;
    }
}
