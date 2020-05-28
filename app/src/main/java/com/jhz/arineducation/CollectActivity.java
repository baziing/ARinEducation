package com.jhz.arineducation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class CollectActivity extends AppCompatActivity {

    private DeletableAdapter adapter;
    private ArrayList<String> text;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        ListView list_view = (ListView) findViewById(R.id.list_view);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text = new ArrayList<String>();
        text.add("111");
        text.add("222");
        text.add("333");
        text.add("444");

        // 初始化数据结束
        adapter = new DeletableAdapter(this, text);
        list_view.setAdapter(adapter);
        // list_view.setSelector(R.drawable.list_select_color);


    }
}
