package com.pigsar.szmj.ui;

import com.pigsar.szmj.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TitleScreenActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.e("MJ", String.format("TitleScreenActivity.onCreate"));
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.title_screen);
	}
}
