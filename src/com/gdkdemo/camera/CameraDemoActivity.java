package com.gdkdemo.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.buaa.network.HttpUtil;
import com.google.android.glass.media.CameraManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;



public class CameraDemoActivity extends Activity
{
    private final int IMAGE_CAPTURE_REQUEST_CODE = 101;

    // For tap event
    private GestureDetector mGestureDetector;

    private String name = "";
    private String birthday = "";
    private String hometown ="";
    private String college= "";
    
    String APP_SDCARD_FOLDER = 
            Environment.getExternalStorageDirectory().getAbsolutePath();
      

    


    @Override
    protected void onDestroy()
    {
        //doUnbindService();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate() called.");

        setContentView(R.layout.activity_start);
        
        System.out.println("sdcard path"+APP_SDCARD_FOLDER);

        // For gesture handling.
        mGestureDetector = createGestureDetector(this);

        // We need a real service.
        // bind does not work. We need to call start() explilicitly...
        // doBindService();
        //doStartService();
        // TBD: We need to call doStopService() when user "closes" the app....
        // ...

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if(data != null) {
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        // Note: Apparently there is currently a bug.
                        // https://developers.google.com/glass/develop/gdk/reference/com/google/android/glass/media/Camera#EXTRA_THUMBNAIL_FILE_PATH
                        String thumbnailFilePath = extras.getString(CameraManager.EXTRA_THUMBNAIL_FILE_PATH);
                        if(Log.I) Log.i("thumbnailFilePath = " + thumbnailFilePath);

                        String pictureFilePath = extras.getString(CameraManager.EXTRA_PICTURE_FILE_PATH);
                        if(Log.D) Log.d("pictureFilePath = " + pictureFilePath);
                        File myuploadimg  = new File(thumbnailFilePath);
                        //System.out.println("file path"+thumbnailFilePath);
                        //System.out.println("file exists"+myuploadimg.exists());
                        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
                        t.setToNow(); 
                        int minute = t.minute;
                        int second = t.second;
                        
                        //System.out.println("start upload time"+minute+"."+second);
                        upload(new File(thumbnailFilePath));
 

                    } else {
                        Log.w("The returned intent does not include extras.");
                    }
                } else {
                    // Can this happen?
                    Log.w("Null Intent data returned.");
                }
            } else {
                Log.i("Request failed: resultCode = " + resultCode);
            }
        }
        // Call super?
        super.onActivityResult(requestCode, resultCode, data);
    }


    // TBD:
    // Just use context menu instead of gesture ???
    // ...

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

     

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
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

    public void upload(File myfile) {

		RequestParams params = new RequestParams();
		//params.put("person_name", trainName);
		try {
			System.out.println("file size"+myfile.length());
			params.put("file", myfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpUtil.post(params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonobject) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonobject);
				String statuscode = null;
				try {
					statuscode = jsonobject.getString("code");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 

				System.out.println("return json data"+jsonobject.toString());
				
				
				try {
					 name = jsonobject.getString("name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
				    birthday = jsonobject.getString("birth_date");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					hometown = jsonobject.getString("hometown");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					college = jsonobject.getString("college");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					Intent intent = new Intent(getApplicationContext(), PersonInfoActivity.class);        
		            intent.putExtra("name", name);
		            intent.putExtra("birthday", birthday);
		            intent.putExtra("hometown", hometown);
		            intent.putExtra("college", college);

		            startActivity(intent);
				
			}

			public void onFailure(Throwable arg0) { // 失败，调用
				System.out.println("onfailure");
				//status = 2;
			}

			public void onFinish() { // 完成后调用，失败，成功，都要掉
				System.out.println("onfinish");
			}

			@Override
			protected void handleFailureMessage(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub

				
				super.handleFailureMessage(arg0, arg1);
				
				  Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
                  t.setToNow(); 
                  int minute = t.minute;
                  int second = t.second;
                  
                  System.out.println("end failure time"+minute+"."+second);
				//status = 2;
				//System.out.println("onfailuremessage" + arg0 + arg1);
				
				String fileName = String.format("%s.html", "liuyuxiaolog");
				    String storepath = APP_SDCARD_FOLDER;
				   // System.out.println("storepath"+storepath);
					try {
						FileOutputStream fos = new FileOutputStream(storepath+"/"+fileName);
					
					System.out.println("store log path "+storepath+fileName);
			       
					File tofile=new File(storepath+"/"+fileName);
					System.out.println("log file exist "+tofile.exists());

					FileWriter fw=new FileWriter(tofile);
					BufferedWriter buffw=new BufferedWriter(fw);
					PrintWriter pw=new PrintWriter(buffw);

					//String str="hellow world";
					pw.println(arg1);

					pw.close();
					buffw.close();
					fw.close();
					}
			 catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				 System.out.println(e.toString());
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				 System.out.println(e.toString());
			}
			};

		},"/postFaceToIdentify/");
	}

}
