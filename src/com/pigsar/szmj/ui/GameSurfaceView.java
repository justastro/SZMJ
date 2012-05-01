package com.pigsar.szmj.ui;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.graphic.RenderManager;
import com.pigsar.szmj.graphic.TextureManager;
import com.pigsar.szmj.library.GameController;
import com.pigsar.szmj.library.GameController.State;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {

	private GameRenderer _renderer;
	
	public GameSurfaceView(Context context) {
		super(context);
		
		setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		
		_renderer = new GameRenderer(context);
		setRenderer(_renderer);
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
				_renderer.gameController().processTouchEvent(event);
			}
		});
		return true;
	}
	
	public void startGame() {
		queueEvent(new Runnable() {
			public void run() {
				_renderer.gameController().transitState(State.StartGame);
			}
		});
	}

}


class GameRenderer implements GLSurfaceView.Renderer {

	private Context _context;
	private GameController _gameCtrl;
	private RenderManager _renderMgr;
	
	public float touchDeltaX;
	public float touchDeltaY;
	
	public GameRenderer(Context context) {
		_context = context;
		_gameCtrl = new GameController();;
		
		_renderMgr = new RenderManager(_gameCtrl);
		_gameCtrl.setRenderManager(_renderMgr);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		TextureManager.instance().initilaize(_context, gl);
		
		_renderMgr.initilaizeResource();
		
		// Set the background frame color
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 1.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(100.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}
	
	private long _prevTime = 0;
	public void onDrawFrame(GL10 gl) {
		// Delta time
		long currentTime = System.currentTimeMillis();
		float time = (currentTime - _prevTime) / 1000.0f;
		_prevTime = currentTime;
			
			// FPS
//			float fps = 60 / t * 1000;
//			Log.d("Frame Rate", String.valueOf(fps) );
		
//		_gameCtrl.update(time);
		
		// Redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        _renderMgr.render(gl, time);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		// make adjustments for screen ratio
	    _renderMgr.setViewport(0, 0, width, height);
	}
	
	public GameController gameController() {
		return _gameCtrl;
	}
}




