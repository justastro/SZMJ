package com.pigsar.szmj.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	
	private GameSurfaceView _surfaceView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.w("MJ", String.format("GameActivity.onCreate"));
        super.onCreate(savedInstanceState);
        
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        _surfaceView = new GameSurfaceView(this);
        setContentView(_surfaceView);
        
        _surfaceView.startGame();
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		Log.w("MJ", String.format("GameActivity.onPause"));
		super.onPause();
		_surfaceView.onPause();
	}

	@Override
	protected void onResume() {
		Log.w("MJ", String.format("GameActivity.onResume"));
		super.onResume();
		_surfaceView.onResume();
	}
    
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}
}