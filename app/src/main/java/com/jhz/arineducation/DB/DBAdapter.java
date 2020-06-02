package com.jhz.arineducation.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jhz.arineducation.model.Keyword;
import com.jhz.arineducation.model.Language;
import com.jhz.arineducation.model.ModelName;

import java.util.ArrayList;

public class DBAdapter {

    private String[] data={
            "1,竹/竹子,bamboo-leaves,zhu4/zhu4.zi5,bamboo",
            "1,仙人掌,cactus-big,xian1.ren2.zhang3,cactus",
            "1,云/云朵,cloud-big,yun2/yun2.duo3,cloud",
            "1,珊瑚,coral-small_orange,shan1.hu2,coral",
            "1,树叶/叶子/叶/落叶,leaf-maple-simple,shu4.ye4/ye4.zi5/ye4/luo4.ye4,leaf",
            "1,蘑菇,mushroom-toadstool,mo2.gu5,mushroom",
            "2,猪/小猪/猪仔,pig,zhu1/xiao3.zhu1/zhu1zai3,pig",
            "1,石/石头,stone-flat,shi2/shi2.tou5,stone",
            "1,树/树木/大树,tree,shu4/shu4.mu4/da4.shu4,tree",
            "1,枯木/死树,tree-dead,ku1.mu4/si3.shu4,deadwood",
            "1,树干/树枝,tree-trunk,shu4.gan4/shu4.zhi1,trunk"
    };

    private Context context;

    DBOpenHelper dbOpenHelper;

    public DBAdapter(Context context){
        this.context=context;
        dbOpenHelper=new DBOpenHelper(context,"data.db",null,1);
        dbOpenHelper.getWritableDatabase();
    }

    public void init(){

        Language chi=new Language(1,"chi_sim");
        Language eng=new Language(2,"eng");
        add(chi);
        add(eng);

        add(toModelName());
    }

    public void add(Language language){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("language_id", language.getLanguage_id());
        contentValues.put("language_name", language.getLanguage_name());
        db.insert("Language",null,contentValues);
        contentValues.clear();
    }

    public void add(Keyword keyword){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("language_id", keyword.getLanguage_id());
        contentValues.put("text", keyword.getText());
        db.insert("Keyword",null,contentValues);
        contentValues.clear();
    }

    public void add(ModelName modelName){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("type",modelName.getType());
        contentValues.put("chi",modelName.getChi());
        contentValues.put("eng",modelName.getEng());
        contentValues.put("model_name",modelName.getModelName());
        db.insert("Model",null,contentValues);
    }

    public void add(ArrayList<ModelName> modelNameArrayList){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        for (int i=0;i<modelNameArrayList.size();i++){
            contentValues.put("type",modelNameArrayList.get(i).getType());
            contentValues.put("chi",modelNameArrayList.get(i).getChi());
            contentValues.put("eng",modelNameArrayList.get(i).getEng());
            contentValues.put("model_name",modelNameArrayList.get(i).getModelName());
            db.insert("Model",null,contentValues);
        }
    }

    public void delele(String text){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        db.delete("Keyword","text=?",new String[]{text});
    }

    public ArrayList<String> search(String tableName,String column){
        ArrayList<String>stringArrayList=new ArrayList<String>();
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.query(tableName,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String text=cursor.getString(cursor.getColumnIndex(column));
                stringArrayList.add(text);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return stringArrayList;
    }

    public String search(String tableName,String language,String keyword,String column){
        ArrayList<String>stringArrayList=new ArrayList<String>();
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.query(tableName,null,language+" like ?",new String[]{"%"+keyword+"%"},null,null,null);
        System.out.println(cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                String text=cursor.getString(cursor.getColumnIndex(column));
                stringArrayList.add(text);
            }while (cursor.moveToNext());
        }
        cursor.close();
        if (cursor.getCount()>0){
            return stringArrayList.get(0);
        }else
            return null;
    }

    public int search(String tableName,String keyword,String column){
        ArrayList<Integer>intArrayList=new ArrayList<Integer>();
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.query(tableName,null,"text=?",new String[]{keyword},null,null,null);
        System.out.println(cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                Integer text=cursor.getInt(cursor.getColumnIndex(column));
                intArrayList.add(text);
            }while (cursor.moveToNext());
        }
        cursor.close();
        if (cursor.getCount()>0){
            return intArrayList.get(0);
        }else
            return 3;
    }

    public boolean isExisting(String tableName,String column,String value){
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
        Cursor cursor=db.query(tableName,null,column+"=?",new String[]{value},null,null,null);
        System.out.println(cursor.getCount());
        System.out.println("________________________________________");
        if (cursor.getCount()>0)
            return true;
        else
            return false;
    }

    private ArrayList<ModelName> toModelName(){
        ArrayList<ModelName> modelNameArrayList=new ArrayList<ModelName>();
        for (int i=0;i<data.length;i++){
            String[] strings=data[i].split(",");
            System.out.println(strings[1]+"_______________");
            try {
                if (strings.length==5){
                    ModelName modelName=new ModelName(Integer.parseInt(strings[0]),strings[1],strings[4],strings[2]);
                    modelNameArrayList.add(modelName);
                }
            }catch (NumberFormatException e){
                System.out.println(e);
            }
        }
        return modelNameArrayList;
    }
}
