package com.pigsar.szmj.graphic.old;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.pigsar.szmj.graphic.RenderManager;
import com.pigsar.szmj.graphic.TextureManager;
import com.pigsar.szmj.library.GameController;



public class OldGameRenderer implements GLSurfaceView.Renderer {

	private Context _context;
	private GameController _gameCtrl;
	private RenderManager _renderMgr;
	private float _aspect;
	
	public ArrayList<Renderable> _bgOrthoRenderables = new ArrayList<Renderable>();
	public ArrayList<Renderable> _tablePerspRenderables = new ArrayList<Renderable>();
	public ArrayList<Renderable> _facingPlayerPerspRenderables = new ArrayList<Renderable>();
	public ArrayList<Renderable> _fgOrthoRenderables = new ArrayList<Renderable>();
	
	public float touchDeltaX;
	public float touchDeltaY;
	
	public OldGameRenderer(Context context, GameController controller) {
		_context = context;
		_gameCtrl = controller;
		_renderMgr = new RenderManager(controller);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		TextureManager.instance().initilaize(_context, gl);
		
		// Set the background frame color
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 1.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(100.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        
		
		
        // Load the texture for the square
		int count;
		count = _bgOrthoRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_bgOrthoRenderables.get(i).loadGLTexture(gl, _context);
        }
        count = _tablePerspRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_tablePerspRenderables.get(i).loadGLTexture(gl, _context);
        }
        count = _facingPlayerPerspRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_facingPlayerPerspRenderables.get(i).loadGLTexture(gl, _context);
        }
        count = _fgOrthoRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_fgOrthoRenderables.get(i).loadGLTexture(gl, _context);
        }
	}
	
	int _counter = 0;
	long _prevTime = 0;
	public void onDrawFrame(GL10 gl) {
		
		if ( _counter % 60 == 0) {
			float t = System.currentTimeMillis() - _prevTime;
			float fps = 60 / t * 1000;
			Log.d("Frame Rate", String.valueOf(fps) );
			_prevTime = System.currentTimeMillis();
		}
		++_counter;
		
		// Redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        int count;
        setOrtho(gl);
        count = _bgOrthoRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_bgOrthoRenderables.get(i).draw(gl);
        }
        
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        setTablePersp(gl);
        count = _tablePerspRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_tablePerspRenderables.get(i).draw(gl);
        }
        
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        setFacingPlayerPersp(gl);
        count = _facingPlayerPerspRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_facingPlayerPerspRenderables.get(i).draw(gl);
        }
        
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        setOrtho(gl);
        count = _fgOrthoRenderables.size();
        for (int i = 0; i < count; ++i) {
        	_fgOrthoRenderables.get(i).draw(gl);
        }
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		// make adjustments for screen ratio
	    _aspect = (float) width / height;
	}
	
	
	
	private void setOrtho(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-_aspect, _aspect, -1.0f, 1.0f, 0.1f, 100.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		GLU.gluLookAt(gl, 0, 0, 5,
						  0, 0, 0,
						  0, 1, 0);
	}
	
	private float _cameraPosY = 75.3f;
	private float _cameraPosZ = 9.82f;
	private void setTablePersp(GL10 gl) {
		_cameraPosY += touchDeltaY * 0.001f;
		_cameraPosZ += touchDeltaX * 0.001f;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, _aspect, 0.1f, 100.0f);
		//gl.glFrustumf(-_aspect, _aspect, -1, 1, 0.1f, 100.0f);  // apply the projection matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// When using GL_MODELVIEW, you must set the camera view
		GLU.gluLookAt(gl, 0, _cameraPosY, _cameraPosZ,
		//GLU.gluLookAt(gl, 0, 75.3f, 9.82f,
						  0, 0, 0,
						  0, 1, 0);
	}
	
//	private float _cameraPosY = 12.09f;
//	private float _cameraPosZ = 36.16f;
	private void setFacingPlayerPersp(GL10 gl) {
//		_cameraPosY += touchDeltaY * 0.001f;
//		_cameraPosZ += touchDeltaX * 0.001f;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, _aspect, 0.1f, 100.0f);
		//gl.glFrustumf(-_aspect*10, _aspect*10, -10, 10, 0.1f, 100.0f);  // apply the projection matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// When using GL_MODELVIEW, you must set the camera view
		//GLU.gluLookAt(gl, 0, _cameraPosY, _cameraPosZ,
		//				  0, _cameraPosY, 0,
		GLU.gluLookAt(gl, 0, 12.09f, 36.16f,
						  0, 12.09f, 0,
						  0, 1, 0);
	}
	
}
