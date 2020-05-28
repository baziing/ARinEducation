package com.jhz.arineducation.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.jhz.arineducation.DB.DBAdapter;
import com.jhz.arineducation.R;
import com.jhz.arineducation.model.Keyword;
import com.jhz.arineducation.model.Pinyin;
import com.jhz.arineducation.model.TTS;

public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private boolean mUserRequestedInstall = true;
    private TextView textView;
    private TextView textView1;
    private ImageButton imageButton;
    private Toolbar toolbar;
    private ImageButton button;
    private DBAdapter dbAdapter;
    private TTS TTS;
    private Pinyin pinyin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        textView=(TextView)findViewById(R.id.textView);
        textView1=(TextView)findViewById(R.id.pinyin) ;
        imageButton=(ImageButton)findViewById(R.id.replay);
        button=(ImageButton)findViewById(R.id.button);
        dbAdapter=new DBAdapter(this);
        TTS=new TTS(this);
        pinyin=new Pinyin();

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        String str=intent.getStringExtra("data");
        String modelName=intent.getStringExtra("modelName");
        modelName=modelName+".sfb";
        TTS.setText(str);
        pinyin.setText(str);
        textView.setText(str);

        Toast.makeText(this,str+"++++++++++++++++++++++++++++++++++++",Toast.LENGTH_LONG);
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

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
//                .setSource(this, Uri.parse("cactus-big.sfb"))
                .setSource(this, Uri.parse(modelName))
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(andyRenderable);
                    andy.select();
                });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TTS.isEmpty()){
                    TTS.setLanguage();
                    TTS.speak();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbAdapter.isExisting("Keyword","text",str)){
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
    protected void onResume() {
        super.onResume();

        //check permission

        // check ARCore
        try {
            switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                case INSTALLED:
                    // Success, create the AR session.
                    break;
                case INSTALL_REQUESTED:
                    // Ensures next invocation of requestInstall() will either return
                    // INSTALLED or throw an exception.
                    mUserRequestedInstall = false;
                    return;
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                    .show();
            return;
        } catch (UnavailableDeviceNotCompatibleException e){
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                    .show();
            return;
        }
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
