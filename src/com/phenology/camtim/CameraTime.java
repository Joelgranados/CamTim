package com.phenology.camtim;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

public class CameraTime extends Activity implements OnClickListener{
  	EditText minutes;
    Button startb;
    private Camera ct_camera;
    private boolean is_ct_active;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        minutes = (EditText) findViewById(R.id.editText2); 
        startb = (Button) findViewById(R.id.button1);
        startb.setOnClickListener(this);
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
    		ct_camera.setPreviewDisplay((SurfaceHolder) vv);
    	} catch (IOException ioe) {return false;}
    	
    	ct_camera.startPreview();
    	return true;
    }

    private void stopCamera (){
    	ct_camera.stopPreview();
    	ct_camera.release();
    }
    
    /** Def button behavior */
    public void onClick (View v){
    	if (this.is_ct_active)
    		this.stopCamera();
    	else
    		this.startCamera();
    }
}