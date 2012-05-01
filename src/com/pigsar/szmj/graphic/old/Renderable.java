package com.pigsar.szmj.graphic.old;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public interface Renderable {

	public void loadGLTexture(GL10 gl, Context context);
	
	
	
	public void draw(GL10 gl);
}
