package com.jhz.arineducation.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;

import java.util.ArrayList;

public class DeletableAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> text;
    private int language=1;
    public DeletableAdapter(Context context,ArrayList<String> text){
        this.context = context;
        this.text=text;
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int position) {
        return text.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index=position;
        View view=convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.item, null);
        }

        final TextView textView=(TextView)view.findViewById(R.id.simple_item_1);
        textView.setText(text.get(position));

        final ImageView imageView=(ImageView)view.findViewById(R.id.simple_item_2);
        imageView.setBackgroundResource(android.R.drawable.ic_delete);
        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str=text.get(index);
                DBAdapter dbAdapter=new DBAdapter(context);
                dbAdapter.delele(str);

                text.remove(index);
                notifyDataSetChanged();
                Toast.makeText(context,"取消收藏成功".toString(), Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView imageView1=(ImageView)view.findViewById(R.id.simple_item_3);
        imageView1.setBackgroundResource(android.R.drawable.ic_menu_search);
        imageView1.setTag(position);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=text.get(index);
                find(str);
            }
        });

        return view;
    }

    public String getLanguage(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("network_url",context.MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if (language.indexOf("eng")!=-1){
            {
                this.language=2;
                return "eng";
            }
        }else{
            this.language=1;
            return "chi";
        }
    }

    public String getLanguage(String str){
        DBAdapter dbAdapter=new DBAdapter(context);
        switch (dbAdapter.search("Keyword",str,"language_id")){
            case 1:
                this.language=1;
                return "chi";
            case 2:
                this.language=2;
                return "eng";
                default:
        }
        SharedPreferences sharedPreferences=context.getSharedPreferences("network_url",context.MODE_PRIVATE);
        String language=sharedPreferences.getString("language","");
        if (language.indexOf("eng")!=-1){
            {
                this.language=2;
                return "eng";
            }
        }else{
            this.language=1;
            return "chi";
        }
    }

    public void find(String text){
        DBAdapter dbAdapter=new DBAdapter(context);
        if ("eng".equals(getLanguage(text))){
            String str=text;
            String string = "";
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (Character.isLetter(ch)) {
                    string = string + ch;
                }
            }
            str=string;
            text=str;
        }
        else {
            String str=text;
            String reg = "[^\u4e00-\u9fa5]";
            str = str.replaceAll(reg, "");
            text=str;
        }

        if (TextUtils.isEmpty(text))
            return;
        else{
            String str=dbAdapter.search("Model",getLanguage(text),text,"model_name");
            if (TextUtils.isEmpty(str)){
                //不存在模型
                putValues(str,false,text,language);
            }else{
                //存在模型
                putValues(str,true,text,language);
            }
        }
    }

    public void putValues(String model,boolean isExisting,String text,int language){
        Intent intent=new Intent();
        intent.putExtra("data",text);
        intent.putExtra("modelName",model);
        intent.putExtra("language",language);
        if (isExisting){
            intent.setClass(context,VideoRecordingActivity.class);
        }else {
            intent.setClass(context,TextActivity.class);
        }
        context.startActivity(intent);
    }

}
