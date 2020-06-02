package com.jhz.arineducation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;
import com.jhz.arineducation.model.Keyword;
import com.jhz.arineducation.model.Pinyin;
import com.jhz.arineducation.model.TTS;

public class TextMarkActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textView1;
    private ImageButton imageButton;
    private Toolbar toolbar;
    private TTS TTS;
    private Pinyin pinyin;
    private ImageButton button;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        textView=(TextView)findViewById(R.id.textView);
        textView.setText("hellp");
        textView1=(TextView)findViewById(R.id.pinyin);
        imageButton=(ImageButton)findViewById(R.id.replay);
        TTS=new TTS(this);
        pinyin=new Pinyin();
        button=(ImageButton)findViewById(R.id.mark);
        dbAdapter=new DBAdapter(this);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        textView.setText(intent.getStringExtra("data"));
        String str=intent.getStringExtra("data");
        int language=intent.getIntExtra("language",1);
        TTS.setText(str);
        pinyin.setText(str);

        if (pinyin.isChi())
            textView1.setText(pinyin.getPinyin());

        Keyword keyword=new Keyword(str,this);

        if (dbAdapter.isExisting("Keyword","text",str)){
//            button.setText("已经收藏");
            button.setImageResource(R.drawable.bookmark_black_36x36);
        }else {
//            button.setText("添加收藏");
            button.setImageResource(R.drawable.bookmark_border_black_36x36);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS.setLanguage(language);
                if (!TTS.isEmpty()){
                    TTS.speak();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbAdapter.isExisting("Keyword","mark",str)){
                    dbAdapter.delele(str);
//                    button.setText("添加收藏");
                    button.setImageResource(R.drawable.bookmark_border_black_36x36);
                }else {
                    dbAdapter.add(keyword);
//                    button.setText("已经收藏");
                    button.setImageResource(R.drawable.bookmark_black_36x36);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
