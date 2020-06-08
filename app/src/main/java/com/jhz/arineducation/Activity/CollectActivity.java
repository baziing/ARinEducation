package com.jhz.arineducation.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;

import java.util.ArrayList;

public class CollectActivity extends AppCompatActivity {

    private DeletableAdapter adapter;
    private ArrayList<String> text;
    private Toolbar toolbar;
    private ArrayList<String>strs;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        ListView list_view = (ListView) findViewById(R.id.list_view);

        strs=dbAdapter.search("Keyword","text");

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化数据结束
        adapter = new DeletableAdapter(this, strs);
        list_view.setAdapter(adapter);


    }
}
