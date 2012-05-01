package com.pigsar.szmj.graphic;

import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class TextureManager {

	static private TextureManager s_manager;
	
	static public TextureManager instance() {
		if (s_manager == null) {
			s_manager = new TextureManager();
		}
		return s_manager;
	}
	
	
	private Context _context;
	private GL10 _gl;
	private Hashtable<Integer,Integer> _idTexMap = new Hashtable<Integer,Integer>();
	
	private TextureManager() {
		
	}
	
	public void initilaize(Context context, GL10 gl) {
		_context = context;
		_gl = gl;
		_idTexMap.clear();
	}
	
	public int load(int resourceId) {
		// Check the texture is already loaded or not
		if ( _idTexMap.containsKey(resourceId) ) {
			return _idTexMap.get(resourceId).intValue();
		}
		
		int[] textures = new int[1];
		try {
			// loading texture
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inScaled = false;
			Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(), resourceId, opt);
			
			// generate one texture pointer and bind it to our array
			_gl.glGenTextures(1, textures, 0);
			_gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			
			// create nearest filtered texture
			_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			_gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
			
			// Use Android GLUtils to specify a two-dimensional texture image from our bitmap 
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Clean up
			bitmap.recycle();
			
			// Cache for further use
			_idTexMap.put(resourceId, textures[0]);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return textures[0];
	}
	
}
