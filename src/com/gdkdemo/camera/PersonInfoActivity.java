package com.gdkdemo.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import com.buaa.network.HttpUtil;
import com.google.android.glass.media.CameraManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;



public class PersonInfoActivity extends Activity
{
    private final int IMAGE_CAPTURE_REQUEST_CODE = 101;

    // For tap event
    private GestureDetector mGestureDetector;

    private String name = " ";
    private String birthday = "";
    private String hometown =" ";
    private String college= " ";
      
   
    private TextView nametext;
    private TextView birthtext;
    private TextView hometowntext;
    private TextView collegetext;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate() called.");
        
        Intent intent = getIntent(); //用于激活它的意图对象
        
		name = intent.getStringExtra("name");
		birthday = intent.getStringExtra("birthday");
		hometown = intent.getStringExtra("hometown");
		college = intent.getStringExtra("college");
		
        setContentView(R.layout.personinfo);

		nametext =(TextView)findViewById(R.id.name);
		birthtext =(TextView)findViewById(R.id.birthday);
		hometowntext =(TextView)findViewById(R.id.hometown);
		collegetext =(TextView)findViewById(R.id.college);


		if(name.equalsIgnoreCase(""))
		{
			nametext.setText("Person not found");
		}
		else{
			nametext.setText("Name："+name);
		}

		if(birthday.equalsIgnoreCase(""))
		{
			birthtext.setText("Unknown");
		}
		else{
			birthtext.setText("Birthday: "+birthday);
		}
		
		if(hometown.equalsIgnoreCase(""))
		{
			hometowntext.setText("Unknown");
		}
		else{
			hometowntext.setText("hometown: "+hometown);
		}
		
		if(college.equalsIgnoreCase(""))
		{
			collegetext.setText("Unknown");
		}
		else{
			collegetext.setText("College: "+college);
		}
		
		

        // For gesture handling.
        mGestureDetector = createGestureDetector(this);

     

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Stop the preview and release the camera.
            // Execute your logic as quickly as possible
            // so the capture happens quickly.
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


   

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private GestureDetector createGestureDetector(Context context)
    {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if(Log.D) Log.d("gesture = " + gesture);
                if (gesture == Gesture.TAP) {
                    handleGestureTap();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    handleGestureTwoTap();
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    handleGestureSwipeRight();
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    handleGestureSwipeLeft();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }



    // Tap triggers photo taking...
    private void handleGestureTap()
    {
        Log.d("handleGestureTap() called.");

     

        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
        
        Intent intent = new Intent(getApplicationContext(), CameraDemoActivity.class);        
      
        startActivity(intent);
    }

    private void handleGestureTwoTap()
    {
        Log.d("handleGestureTwoTap() called.");

        // Quit
        this.finish();
    }

    private void handleGestureSwipeRight()
    {
        Log.d("handleGestureSwipeRight() called.");


    }

    private void handleGestureSwipeLeft()
    {
        Log.d("handleGestureSwipeLeft() called.");


    }

   

}
