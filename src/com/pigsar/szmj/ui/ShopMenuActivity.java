package com.pigsar.szmj.ui;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.SoundManager;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ShopMenuActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.shop_menu);
		
		ImageButton button;
		button = (ImageButton)findViewById(R.id.backButton);
		button.setOnClickListener(backButton_onClickListener);
	}
	
	private OnClickListener backButton_onClickListener = new OnClickListener() {
		public void onClick(View v) {
			onBackPressed();
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade, R.anim.hold);
		SoundManager.instance().playButton();
	}

}
