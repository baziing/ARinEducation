package com.jhz.arineducation;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARActivity extends AppCompatActivity {

//    private static final String TAG = HelloSceneformActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ModelRenderable.builder()
                .setSource(this, Uri.parse("mosaic ball.sfb"))
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

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
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

    }
}
