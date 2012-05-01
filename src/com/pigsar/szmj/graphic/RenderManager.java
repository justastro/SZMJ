package com.pigsar.szmj.graphic;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.library.GameController;

public class RenderManager {
	
	private GameController _gameCtrl;
	private BackgroundRenderer _bgRenderer; 
	private TileRenderer _tileRenderer;
	private int[] _viewport = new int[4];
	
	public RenderManager(GameController controller) {
		_gameCtrl = controller;
		_bgRenderer = new BackgroundRenderer(this);
		_tileRenderer = new TileRenderer(this);
	}
	
	public GameController gameController() {
		return _gameCtrl;
	}
	
	public TileRenderer tileRenderer() {
		return _tileRenderer;
	}
	
	public float aspectRatio() {
		return (float)_viewport[2]/_viewport[3];
	}
	
	public int[] viewport() {
		return _viewport;
	}
	
	public void setViewport(int x, int y, int width, int height) {
		_viewport[0] = x;
		_viewport[1] = y;
		_viewport[2] = width;
		_viewport[3] = height;
	}
	
	public void initilaizeResource() {
		_bgRenderer.initializeResource();
		_tileRenderer.initializeResource();
	}
	
	public void render(GL10 gl, float time) {
		_bgRenderer.render(gl, time);
		_tileRenderer.render(gl, time);
	}
}
