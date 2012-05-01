package com.pigsar.szmj.graphic;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

import com.pigsar.szmj.library.AbstractPlayer;
import com.pigsar.szmj.library.GameController;
import com.pigsar.szmj.library.Tile;

/***
 * 
 * @author justinleung
 *
 */
public class TileRenderer {

	public static final float TILE_WIDTH = 3.0f;
	public static final float TILE_WIDTH_2 = TILE_WIDTH / 2;
	public static final float TILE_HEIGHT = 4.2f;
	public static final float TILE_HEIGHT_2 = TILE_HEIGHT / 2;
	public static final float TILE_LENGTH = 2.0f;
	public static final float TILE_LENGTH_2 = TILE_LENGTH / 2;

	public static final float TILE_RELAX_RATIO = 1.03f;
	public static final float TILE_WIDTH_RELAXED = TILE_WIDTH * TILE_RELAX_RATIO;
	public static final float TILE_WIDTH_2_RELAXED = TILE_WIDTH_2 * TILE_RELAX_RATIO;
	public static final float TILE_HEIGHT_RELAXED = TILE_HEIGHT * TILE_RELAX_RATIO;
	public static final float TILE_HEIGHT_2_RELAXED = TILE_HEIGHT_2 * TILE_RELAX_RATIO;
	public static final float TILE_HEIGHT_SELECTED = TILE_HEIGHT / 3;
	public static final float TILE_NEWLY_DRAWED_OFFSET = TILE_WIDTH / 4;
	
	//private static final float CAMERA_POS_Y = 96.0f;
	//private static final float CAMERA_POS_Z = 30.0f;
	public static final float CAMERA_POS_Y = 72.0f;
	public static final float CAMERA_POS_Z = 22.5f;
	public static final float CAMERA_CENTER_Y = 0.0f;
	public static final float CAMERA_CENTER_Z = 2.0f;
	public static final float CAMERA_FOV = (float)(Math.PI/4);				// 45 degree
	public static final float CAMERA_NEAR = 0.1f;
	public static final float CAMERA_FAR = 1000.0f;
	
	public static final float PLAYER_TILE_Z_HORI = 33.0f;
	public static final float PLAYER_TILE_Z_VERT = 25.0f;
	public static final float USER_PLAYER_TILE_X_START =
		(GameController.PLAYER_TILE_NUM * TILE_WIDTH_RELAXED + TILE_NEWLY_DRAWED_OFFSET) * -0.5f +
		TILE_WIDTH_2;
	//private static final float USER_PLAYER_TILE_Y = 40.0f;
	public static final float USER_PLAYER_TILE_Y = 36.0f;
	//private static final float USER_PLAYER_TILE_Z = 27.0f;
	public static final float USER_PLAYER_TILE_Z = 24.0f;
	public static final float USER_PLAYER_TILE_ROT_X =
		(float)(Math.atan2(CAMERA_POS_Z - CAMERA_CENTER_Z, CAMERA_POS_Y - CAMERA_CENTER_Y) * 180 / Math.PI);
	
	public static final int DISCARDED_TILE_COLUMN_COUNT = 8;
	public static final float DISCARDED_TILE_CENTER_OFFSET_Z_HORI = 16.0f;
	public static final float DISCARDED_TILE_CENTER_OFFSET_Z_VERT = 10.0f;
	public static final float DISCARDED_TILE_X_START =
		-(DISCARDED_TILE_COLUMN_COUNT * 0.5f) * TILE_WIDTH_RELAXED + TILE_WIDTH_2_RELAXED;

	private RenderManager _renderMgr;
	private TileFaceRenderer _tileFaceRenderer;
	private TileBodyRenderer _tileBodyRenderer;
	
	private Hashtable<Tile,TileObject> _tileObjHash = new Hashtable<Tile,TileObject>();
	
	private float[] _projMatrix = new float[16];
	private float[] _viewMatrix = new float[16];
	
	public TileRenderer(RenderManager manager) {
		_renderMgr = manager;
		
		_tileFaceRenderer = new TileFaceRenderer(this);
		_tileBodyRenderer = new TileBodyRenderer(this);
	}
	
	public void initializeResource() {
		_tileFaceRenderer.initializeResource();
		_tileBodyRenderer.initializeResource();
	}
	
	public float[] projectionMatrix() {
		return _projMatrix;
	}
	
	public float[] viewMatrix() {
		return _viewMatrix;
	}
	
	public void render(GL10 gl, float time) {
		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
		
		setCamera(gl);
		
		for (AbstractPlayer player : _renderMgr.gameController().players()) {
			ArrayList<Tile> tiles = player.tiles();
			
			for (Tile tile : tiles) {
				tileObject(tile).tick(time);
			}
			
			_tileBodyRenderer.render(gl, tiles);
			_tileFaceRenderer.render(gl, tiles);
		}
	}
	
	public TileObject tileObject(Tile tile) {
		TileObject obj = _tileObjHash.get(tile);	assert(obj != null);
		if (obj == null) {
			obj = new TileObject(tile);
			obj.addListener(_renderMgr.gameController());
			_tileObjHash.put(tile, obj);
		}
		return obj;
	}

	private void setCamera(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		// Make frustum from perspective
		float tangent = (float)Math.tan(CAMERA_FOV/2.0f);
		float height = CAMERA_NEAR * tangent;
		float width = height * _renderMgr.aspectRatio();
		Matrix.frustumM(_projMatrix, 0, -width, width, -height, height, CAMERA_NEAR, CAMERA_FAR);
		gl.glLoadMatrixf(_projMatrix, 0);
		
		// View
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		Matrix.setLookAtM(_viewMatrix, 0,
						  0, CAMERA_POS_Y, CAMERA_POS_Z,
						  0, CAMERA_CENTER_Y, CAMERA_CENTER_Z,
						  0, 1, 0);
		gl.glLoadMatrixf(_viewMatrix, 0);
	}
	
}




