package com.phenology.camtim;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

public class CameraTime extends Activity
implements OnClickListener, Camera.PictureCallback{
  	EditText minutes;
    Button startb;
    private Camera ct_camera;
    private boolean is_ct_active; //is camera on
    private boolean is_ct_running; //are we taking pictures
    private String image_name;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        minutes = (EditText) findViewById(R.id.editText2); 
        startb = (Button) findViewById(R.id.button1);
        startb.setOnClickListener(this);
        is_ct_active = false;
        image_name = "nothing.jpg";
    }

    private int getMinutes (){
    	try{
    		return Integer.parseInt(minutes.getText().toString());
    	}catch (NumberFormatException nfe) {return 60;}
    } 

    private boolean startCamera (){
    	ct_camera = Camera.open();

    	Camera.Parameters p = ct_camera.getParameters();
    	ct_camera.setParameters(p);
    	
    	VideoView vv = (VideoView) findViewById(R.id.videoView1);
    	try{
    		ct_camera.setPreviewDisplay(vv.getHolder());
    	} catch (IOException ioe) {return false;}
    	
    	ct_camera.startPreview();
    	is_ct_active = true;
    	return true;
    }

    private void stopCamera (){
    	ct_camera.stopPreview();
    	ct_camera.release();
    	this.is_ct_active = false;
    }

    /** This is the start/stop button behavior */
    public void onClick (View v){
    	ct_cam_run();
    }
    
    private void ct_cam_run (){
  		startCamera();
  		try { //wait for the camera to adjust.
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.ct_camera.takePicture(null, this, this);
		
	  	try { //wait for the camera to adjust.
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		stopCamera();
    }

	public void onPictureTaken(byte[] data, Camera camera) {
		Log.e("callback", "entering callback");
		if (data != null) // Skip if no data.
		{
			Log.e("callback", "executing storage");
			this.StoreByteImage(data, 100, this.image_name);
		}
	}
 
    public boolean StoreByteImage(byte[] imageData, int quality, String expName) {

    	// Using sd card. on a level 7.
    	File root_path = Environment.getExternalStorageDirectory();
    	File file_path = new File(root_path.getAbsolutePath()+"/Android/data/"+this.getPackageName()+"/files/"+expName);
    	//file_path.mkdirs();
        FileOutputStream fos = null;
    	
    	
    	Log.e("store", "enter store");
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 5;

            Log.e("store", "decoding");
            Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
                    imageData.length,options);

            Log.e("store", "sending to outputstream");
            fos = new FileOutputStream(file_path, true);

            BufferedOutputStream bos = new BufferedOutputStream(fos);

            Log.e("store", "compressing");
            myImage.compress(CompressFormat.JPEG, quality, bos);

            bos.flush();
            bos.close();
        
        } catch (FileNotFoundException e) {
        	Log.e("error", "file not found");
            Log.e("error",e.getMessage());
        } catch (IOException e) {
        	Log.e("error", "general exception");
            Log.e("error",e.getMessage());
        }
        
        return true;
    }
}