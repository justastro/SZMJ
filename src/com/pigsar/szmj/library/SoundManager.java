package com.pigsar.szmj.library;

import java.util.Hashtable;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.pigsar.szmj.R;

public class SoundManager {

	private static SoundManager s_instance;
	
	private Context _context;
	private SoundPool _soundPool;
	private Hashtable<String,Integer> _soundMap = new Hashtable<String,Integer>();
	
	public static SoundManager instance() {
		if (s_instance == null) {
			s_instance = new SoundManager();
		}
		return s_instance;
	}
	
	public void initialize(Context context) {
		_context = context;
		_soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		_soundMap.put("button", _soundPool.load(context, R.raw.mj_src_se_button, 1));
		_soundMap.put("money", _soundPool.load(context, R.raw.mj_src_se_money, 1));
		_soundMap.put("discard", _soundPool.load(context, R.raw.mj_src_se_hit, 1));
	}
	
	private float getVolume() {
		/* Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0 */
	    AudioManager mgr = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
		return volume;
	}
	
	private void playOnce(String name) {
		if (_soundMap.containsKey(name)) {
			float volume = getVolume();
			_soundPool.play(_soundMap.get(name), volume, volume, 1, 0, 1);
		} else {
			Log.e("SoundManager", String.format("Sound '%s' not found.", name));
		}
	}
	
	public void playButton() {
		playOnce("button");
	}
	
	public void playMoney() {
		playOnce("money");
	}
	
	public void playDiscard() {
		playOnce("discard");
	}
}
