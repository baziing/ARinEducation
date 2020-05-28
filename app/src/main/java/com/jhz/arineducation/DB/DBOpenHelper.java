package com.jhz.arineducation.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_MODEL="create table Model (" +
            "type integer," +
            "chi text," +
            "eng text," +
            "model_name text)";
    public static final String CREATE_LANGUAGE="create table Language (" +
            "language_id integer," +
            "language_name text)";
    public static final String CREATE_KEYWORD="create table Keyword (" +
            "text text," +
            "language_id integer," +
            "logtime TIMESTAMP default (datetime('now', 'localtime')))";

    private Context context;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
//        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MODEL);
        db.execSQL(CREATE_KEYWORD);
        db.execSQL(CREATE_LANGUAGE);
//        Toast.makeText(context,"succeed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
