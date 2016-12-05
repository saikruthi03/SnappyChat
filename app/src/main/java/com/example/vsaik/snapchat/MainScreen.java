package com.example.vsaik.snapchat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.width;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainScreen extends AppCompatActivity {

    private Camera mCamera;
    private CameraView mCameraView ;
    private ImageView capture;
    private Context context ;
    private Bitmap currentBitMap;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    ImageButton flora, fauna;
    Intent go;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data,Camera camera) {
            String content = Base64.encodeToString(data,0);
            mCamera.stopPreview();
            postProcess(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        View contentView = (View)findViewById(R.id.mainscreen_content);
        onStart();
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                Intent main = new Intent(MainScreen.this,ChatActivity.class);
                startActivity(main);
            }
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(MainScreen.this,MainActivity.class);
                startActivity(main);

            }
        });
        }

    @Override
    protected void onStart() {
        super.onStart();
        currentBitMap = null;
        context = this;
        capture = (ImageView) findViewById(R.id.click);
        capture.setX(500);
        capture.setY(550);
        capture.setBackgroundResource(R.drawable.click);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null,null,mPicture);
            }
        });
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
            else {
                mCamera = Camera.open();
                mCamera.startPreview();
            }
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.mainscreen_content);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
    }

    public void postProcess(byte[] picture){
        Bitmap bm = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        int width = bm.getWidth();
        int height = bm.getHeight();
        Matrix matrix =new Matrix();
        matrix.postRotate(90);
        currentBitMap = Bitmap.createBitmap(bm, 0, 0,
                width, height, matrix, true);
        FrameLayout main_layout = (FrameLayout) findViewById(R.id.mainscreen_content);
        main_layout.removeAllViews();
        capture.setBackgroundResource(0);
        main_layout.setBackground(new BitmapDrawable(getResources(),currentBitMap));
        TextView discard = (TextView) findViewById(R.id.discard);
        discard.setX(10);
        discard.setY(1450);
        discard.setTextColor(Color.WHITE);
        discard.setTextSize(20);
        discard.setText("DISCARD");
        discard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onStart();
            }
        });

        TextView accept = (TextView) findViewById(R.id.accept);
        accept.setX(850);
        accept.setY(1450);
        accept.setTextColor(Color.WHITE);
        accept.setTextSize(20);
        accept.setText("ACCEPT");
        accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Accept",Toast.LENGTH_SHORT).show();
                askForCaption();
            }
        });
    }

    private void askForCaption(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(context);
        alert.setTitle("Caption");
        alert.setView(edittext);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable YouEditTextValue = edittext.getText();
                Toast.makeText(context,edittext.getText().toString(),Toast.LENGTH_SHORT).show();
                // send it to cloud
            }
        });
        alert.show();
    }

    public void onBackPressed() {
        Intent mainActivity = new Intent(MainScreen.this,MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}