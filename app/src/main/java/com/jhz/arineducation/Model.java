package com.jhz.arineducation;

import org.litepal.crud.LitePalSupport;

public class Model extends LitePalSupport {
    private int type;
    private String characters;
    private String modelName;

    Model(int type,String characters,String modelName){
        this.type=type;
        this.characters=characters;
        this.modelName=modelName;
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
}
