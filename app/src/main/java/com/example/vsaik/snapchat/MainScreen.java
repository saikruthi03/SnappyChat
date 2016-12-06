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
import android.graphics.Paint;
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
import java.util.ArrayList;
import java.util.List;

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
    private Bitmap mutable;
    private View contentView = null;
    private int index = 0;
    String userId= null;
    private List<BitmapCollection> overlays =  null;
    private FrameLayout main_layout = null;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    ImageButton flora, fauna;
    Intent go;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data,Camera camera) {
            mCamera.stopPreview();
            postProcess(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        userId= getIntent().getStringExtra("UserId");;

        }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main_screen);
        contentView = (View)findViewById(R.id.mainscreen_content);
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                Intent main = new Intent(MainScreen.this,TimeLineActivity.class);
                startActivity(main);
            }
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(MainScreen.this,ChatActivity.class);
                startActivity(main);


            }
            @Override
            public void onSwipeBottom() {
                Intent main = new Intent(MainScreen.this,MainActivity.class);
                getIntent().putExtra("userId",userId);
                startActivity(main);


            }
        });
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
                capture.setOnClickListener(null);
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

        contentView.setOnTouchListener(null);
        try {
            Bitmap bm = BitmapFactory.decodeByteArray(picture, 0, picture.length);

            //bm = ImageUtils.compress(bm);
            int width = bm.getWidth();
            int height = bm.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            currentBitMap = Bitmap.createBitmap(bm, 0, 0,
                    width, height, matrix, true);
            bm = null;
            mutable = currentBitMap.copy(Bitmap.Config.ARGB_8888, true);
            main_layout = (FrameLayout) findViewById(R.id.mainscreen_content);
            main_layout.removeAllViews();

            capture.setBackgroundResource(0);


            main_layout.setBackground(new BitmapDrawable(getResources(), currentBitMap));
            final TextView discard = (TextView) findViewById(R.id.discard);
            final TextView accept = (TextView) findViewById(R.id.accept);
            discard.setX(10);
            discard.setY(1450);
            discard.setTextColor(Color.WHITE);
            discard.setTextSize(20);
            discard.setText("DISCARD");
            discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentBitMap = null;
                    discard.setText("");
                    accept.setText("");
                    onStart();
                }
            });

            accept.setX(850);
            accept.setY(1450);
            accept.setTextColor(Color.WHITE);
            accept.setTextSize(20);
            accept.setText("ACCEPT");
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discard.setText("");
                    accept.setText("");
                    Toast.makeText(context, "Accept", Toast.LENGTH_SHORT).show();
                    askForCaption();
                }
            });

            main_layout.setOnTouchListener(new OnSwipeTouchListener(context) {
                @Override
                public void onSwipeLeft() {
                    Toast.makeText(getApplicationContext(),index+"",Toast.LENGTH_SHORT).show();
                    if(index <= 0)
                        index = overlays.size();
                    floatImage(--index);
                }
                @Override
                public void onSwipeRight() {
                    Toast.makeText(getApplicationContext(),index+"",Toast.LENGTH_SHORT).show();

                    if(index == overlays.size()-1)
                        index = -1;
                    floatImage(++index);
                }
            });
        }
        catch(OutOfMemoryError e){
            Toast.makeText(getApplicationContext(),"Got out of memory error, please try again",Toast.LENGTH_SHORT).show();
            onStart();
        }
    }

    private void floatImage(int imagePointer){
        try {
            main_layout.setBackgroundResource(0);
            BitmapCollection currentFilter = overlays.get(imagePointer);
            mutable = Bitmap.createBitmap(currentBitMap.getWidth(), currentBitMap.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mutable);
            canvas.drawBitmap(currentBitMap, new Matrix(), null);
            canvas.drawBitmap(currentFilter.getBitmap(), currentFilter.getX(), currentFilter.getY(), null);
            main_layout.setBackground(new BitmapDrawable(getResources(), mutable));
            canvas = null;
            currentFilter = null;
        }
        catch(OutOfMemoryError error) {
            Toast.makeText(getApplicationContext(),"Out Of Memory Error",Toast.LENGTH_SHORT).show();
        }
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

    private void init(){
        overlays = new ArrayList<BitmapCollection>();
        BitmapCollection bitmap1 = new BitmapCollection();
        bitmap1.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.spartan));
        bitmap1.setX(1900);
        bitmap1.setY(3600);

        BitmapCollection bitmap2 = new BitmapCollection();
        bitmap2.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sjsu));
        bitmap2.setX(200);
        bitmap2.setY(100);

        BitmapCollection bitmap3 = new BitmapCollection();
        bitmap3.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.photos));
        bitmap3.setX(1000);
        bitmap3.setY(100);

        BitmapCollection bitmap4 = new BitmapCollection();
        bitmap4.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mobile_geek));
        bitmap4.setX(1900);
        bitmap4.setY(1000);

        overlays.add(bitmap1);
        overlays.add(bitmap2);
        overlays.add(bitmap3);
        overlays.add(bitmap4);
    }
}



