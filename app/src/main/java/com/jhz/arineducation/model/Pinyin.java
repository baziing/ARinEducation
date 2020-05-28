package com.jhz.arineducation.model;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;

public class Pinyin {
    String input;
    String tone;
    String output;
    char[] inputs=null;


    public Pinyin(String string){
        setInput(string);
    }

    public void setInput(String input) {
        setInputs(input);
        this.input = input;

        if (input!=null){
            String str="  ";
            for (int i=0;i<this.inputs.length;i++){
                str=str+this.inputs[i]+"  ";
            }
            setOutput(str);
        }
    }

    public void setInputs(String input){
        this.inputs=input.toCharArray();
        setTone();
    }

    public void setTone(){

        ArrayList<String>arrayList=new ArrayList<String>();
        if (this.inputs!=null){
            for (int i=0;i<this.inputs.length;i++){
                arrayList.add(getTone(this.inputs[i]));
            }
            String pinyin="  ";
            for (int i=0;i<arrayList.size();i++){
                pinyin=pinyin+arrayList.get(i)+"  ";
            }
            this.tone=pinyin;
        }
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTone(char c){
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

    public String getTone() {
        return tone;
    }

    public String getOutput() {
        return output;
    }

    public char[] getInputs() {
        return inputs;
    }

    public String getInput() {
        return input;
    }
}
