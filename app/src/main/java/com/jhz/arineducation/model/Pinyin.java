package com.jhz.arineducation.model;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;

public class Pinyin {

    String text;

    public Pinyin(){}

    public Pinyin(String text){
        this.text=text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChi(){
        String str=this.text;
        String reg = "[^\u4e00-\u9fa5]";
        str = str.replaceAll(reg, "");
        this.text=str;
        if (TextUtils.isEmpty(str))
            return false;
        else
            return true;
    }


    public String getPinyin(){
        char[] inputs=text.toCharArray();
        ArrayList<String> arrayList=new ArrayList<String>();

        for(int i=0;i<inputs.length;i++){
            arrayList.add(getPinyin(inputs[i]));
        }

        String pinyin="  ";
        for (int i=0;i<arrayList.size();i++){
            pinyin=pinyin+arrayList.get(i)+"  ";
        }

        return pinyin;
    }

    public String getPinyin(char c){
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


}
