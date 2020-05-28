package com.jhz.arineducation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeletableAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> text;
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
        return view;
    }
}
