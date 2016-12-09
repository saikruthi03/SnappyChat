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
import android.os.AsyncTask;
import android.provider.ContactsContract;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String caption;
    private List<BitmapCollection> overlays =  null;
    private FrameLayout main_layout = null;
    String myName = "jay";
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
        Intent i = getIntent();
        if(i != null)
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
                //onStart();
            }
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
                caption = YouEditTextValue.toString();
                Toast.makeText(context,edittext.getText().toString(),Toast.LENGTH_SHORT).show();
                pushImageToTimeLine();
            }
        });
        alert.show();
    }

    private void pushImageToTimeLine() {

        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("username",myName);
        //currentBitMap = ImageUtils.compress(currentBitMap);
        hashMap.put("pictures",ImageUtils.getStringImage(currentBitMap));
        hashMap.put("caption",caption);
        PushContent pushContent = new PushContent(hashMap);
        pushContent.execute();

        Toast.makeText(context,"Added to timeline",Toast.LENGTH_SHORT).show();

        onStart();

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
        bitmap3.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sharks));
        bitmap3.setX(1200);
        bitmap3.setY(3000);

        BitmapCollection bitmap4 = new BitmapCollection();
        bitmap4.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cali));
        bitmap4.setX(200);
        bitmap4.setY(3000);

        BitmapCollection bitmap5 = new BitmapCollection();
        bitmap5.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.drow));
        bitmap5.setX(1300);
        bitmap5.setY(300);

        BitmapCollection bitmap6 = new BitmapCollection();
        bitmap6.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dota));
        bitmap6.setX(1000);
        bitmap6.setY(200);

        BitmapCollection bitmap7 = new BitmapCollection();
        bitmap7.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.epl));
        bitmap7.setX(2500);
        bitmap7.setY(3000);
        overlays.add(bitmap1);
        overlays.add(bitmap2);
        overlays.add(bitmap3);
        overlays.add(bitmap4);
        overlays.add(bitmap5);
        overlays.add(bitmap6);
        overlays.add(bitmap7);
    }

    class PushContent extends
            AsyncTask<Void, Void, Void> {

        boolean err = false;
        HashMap<String,String> hashMap = null;

        public PushContent(HashMap hashMap){
            this.hashMap = hashMap;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            hashMap.put("URL", Constants.URL + "/insert_picture_stories");
            hashMap.put("Method", "POST");
            PostData fecth = new PostData(hashMap);
            try {
                fecth.doInBackground();
            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(err)
                reportError();
            else{
                onStart();
            }

        }
    }

    private void reportError(){
        Toast.makeText(getApplicationContext(),"Error occured, please try again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                            int[] grantResults) {

        if (requestCode == 50) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                mCamera = Camera.open();
                mCamera.startPreview();
            } else {
                onStart();
            }
        }
    }
            // END_INCLUDE(permission_result)
}



