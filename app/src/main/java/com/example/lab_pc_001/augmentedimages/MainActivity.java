package com.example.lab_pc_001.augmentedimages;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.NodeParent;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.example.lab_pc_001.augmentedimages.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private CustomArFragment arFragment;
    private boolean shouldAddModel = true;
    private boolean shouldAddModel1 = true;
    public AnchorNode anchorNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdateFrame);

    }

    public boolean setupAugmentedImageDb(Config config, Session session){
        AugmentedImageDatabase augmentedImageDatabase;

      try(InputStream is = getAssets().open("imagedatabase.imgdb")){
          augmentedImageDatabase = AugmentedImageDatabase.deserialize(session,is);
      }catch (IOException e){
          Log.e("Failure!","Failed to create imgdb",e);
          augmentedImageDatabase = null;
      }

        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }



    private void onUpdateFrame(FrameTime frameTime){
        Frame frame = arFragment.getArSceneView().getArFrame();
       // Anchor an= this.anchorNode.getAnchor();
        //shouldAddModel = true;

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : augmentedImages){
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING){

               if ((augmentedImage.getName().equals("DEAL3.png") ) && shouldAddModel){

                    //if ((augmentedImage.getName().equals("DEAL3.png"))) {
                        Collection<Anchor> allanchors=augmentedImage.getAnchors();
                        /*if (allanchors.isEmpty()==false)
                        {
                            for (Anchor anchor:allanchors)
                            {
                                augmentedImage.getAnchors().clear();

                            }
                        }*/

                    placeObject(arFragment,
                            augmentedImage.createAnchor(augmentedImage.getCenterPose()),
                            Uri.parse("Airplane.sfb"));
                    shouldAddModel = false;
                    shouldAddModel1=true;
                }

                if ((augmentedImage.getName().equals("airplane.jpg") ) && shouldAddModel1){
                    //if ((augmentedImage.getName().equals("airplane.jpg") )){
                     /*   Collection<Anchor> allanchors1=augmentedImage.getAnchors();

                        if (allanchors1.isEmpty()==false)
                        {
                            for (Anchor anchor:allanchors1)
                            {
                                augmentedImage.getAnchors().clear();
                            }
                        }*/

                    placeObject(arFragment,
                            augmentedImage.createAnchor(augmentedImage.getCenterPose()),
                            Uri.parse("1405 Plane.sfb"));
                    shouldAddModel1 = false;
                    shouldAddModel=true;
                }




            }


        }

    }


    private void placeObject(ArFragment fragment, Anchor anchor, Uri model){

        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable){
if(this.anchorNode!=null)
{
    if(anchorNode.getAnchor()!=null)
    {
        anchorNode.getAnchor().detach();
    }
    anchorNode.setParent(null);
}
         anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);

        node.select();
    }

}
