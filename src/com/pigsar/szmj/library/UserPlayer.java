package com.pigsar.szmj.library;

import com.pigsar.szmj.graphic.RenderManager;
import com.pigsar.szmj.graphic.TileObject;
import com.pigsar.szmj.graphic.TileRenderer;
import com.pigsar.szmj.library.GameController.State;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.view.MotionEvent;


public class UserPlayer extends AbstractPlayer {
	
	private Tile _dragTile;
	private float _dragStartWinY;
	private float _dragPrevWinY;

	public UserPlayer(GameController controller) {
		super(controller);
	}

	@Override
	public void selectActionTile() {
		// Do nothing. Wait for user input.
	}
	
	public void processEvent(MotionEvent event) {
		int action = event.getAction();
		float winX = event.getX();
		float winY = _gameCtrl.renderManager().viewport()[3] - event.getY();
		TileRenderer tr = _gameCtrl.renderManager().tileRenderer();
		
		if (_gameCtrl.state() == State.SelectActionTile) {
			// Select tile
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
				Tile tile = selectTile(winX, winY);
				if (tile != null && _dragTile != tile) {
					if (_dragTile != null) {
						TileObject prevTileObj = tr.tileObject(_dragTile);
						prevTileObj.setTransformSelectable(this, false);
					}
					TileObject tileObj = tr.tileObject(tile);
					tileObj.setTransformSelectable(this, false, 0.0f);
					Matrix.translateM(tileObj.transform, 0, 0, TileRenderer.TILE_HEIGHT_SELECTED, 0);
					
					_dragTile = tile;
					_dragStartWinY = winY;
					_dragPrevWinY = _dragStartWinY;
					return;
				}
			}
			
			// Drag tile
			if (action == MotionEvent.ACTION_MOVE) {
				if (_dragTile != null) {
					if (winY < _dragStartWinY) winY = _dragStartWinY;
					float deltaY = winY - _dragPrevWinY;
					_dragPrevWinY = winY;
					
					TileObject dragTileObj = tr.tileObject(_dragTile);
					Matrix.translateM(dragTileObj.transform, 0, 0, deltaY * dragTileWinToObjRatio(), 0);
					
					// TEMP. Transit state here :)
					if (_dragPrevWinY >= 100) {		// TEMP!!!
						_gameCtrl.transitState(State.ShowActionTile);
						discardTile(_dragTile);
						_dragTile = null;
					}
				}
			}
		}
	}
	
	private float dragTileWinToObjRatio() {
		return 0.055f;
	}
	
	private float[] _winMinMax = new float[8];
	private float[] _modelView = new float[16];
	
	private Tile selectTile(float winX, float winY) {
		RenderManager rm = _gameCtrl.renderManager();
		TileRenderer tr = rm.tileRenderer();
		
		for (Tile tile : selectableTiles()) {
			TileObject tileObj = tr.tileObject(tile);
			
			// TODO: Pre-calc model-view matrix
			Matrix.multiplyMM(_modelView, 0,
					tr.viewMatrix(), 0,
					tileObj.transform, 0);
			
			GLU.gluProject(			// Object min
					-TileRenderer.TILE_WIDTH_2,	-TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
					_modelView, 0, tr.projectionMatrix(), 0,
					rm.viewport(), 0, _winMinMax, 0);
			
			GLU.gluProject(			// Object max
					TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
					_modelView, 0, tr.projectionMatrix(), 0,
					rm.viewport(), 0, _winMinMax, 4);
			
			if (winX > _winMinMax[0] && winY > _winMinMax[1] &&
				winX < _winMinMax[4] && winY < _winMinMax[5])
			{
				return tile;
			}
		}
		return null;
	}
}
