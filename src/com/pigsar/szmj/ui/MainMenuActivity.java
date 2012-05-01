package com.pigsar.szmj.ui;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.SoundManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.w("MJ", String.format("MainMenuActivity.onCreate"));
		
		super.onCreate(savedInstanceState);
		
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main_menu);
		
		// Sound
		SoundManager.instance().initialize(getApplicationContext());
		
		ImageButton button;
		button = (ImageButton)findViewById(R.id.startGameButton);
		button.setOnClickListener(startGameButton_onClickListener);
		button = (ImageButton)findViewById(R.id.shopButton);
		button.setOnClickListener(shopButton_onClickListener);
		button = (ImageButton)findViewById(R.id.recordButton);
		button.setOnClickListener(recordButton_onClickListener);
	}

	private OnClickListener startGameButton_onClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
			proceed(intent);
		}
	};
	
	private OnClickListener shopButton_onClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainMenuActivity.this, ShopMenuActivity.class);
			proceed(intent);
		}
	};
	
	private OnClickListener recordButton_onClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(MainMenuActivity.this, RecordMenuActivity.class);
			proceed(intent);
		}
	};
	
	private void proceed(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		SoundManager.instance().playButton();
	}
}
