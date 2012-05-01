package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.Collections;

import com.pigsar.szmj.graphic.TileObject;
import com.pigsar.szmj.graphic.TileRenderer;

public abstract class AbstractPlayer {

	public static final Seat[] SEAT_VALUES = Seat.values();

	public enum Seat {
		Bottom, Right, Top, Left
	}

	protected GameController _gameCtrl;
	protected ArrayList<Tile> _tiles = new ArrayList<Tile>(38);
	protected ArrayList<Tile> _selectableTiles = new ArrayList<Tile>(14);
	protected ArrayList<Tile> _discardedTiles = new ArrayList<Tile>(38);
	protected Seat _seat;

	public AbstractPlayer(GameController controller) {
		_gameCtrl = controller;
	}

	public ArrayList<Tile> tiles() {
		_tiles.clear();
		_tiles.addAll(_selectableTiles);
		_tiles.addAll(_discardedTiles);
		return _tiles;
	}

	public ArrayList<Tile> selectableTiles() {
		return _selectableTiles;
	}

	public ArrayList<Tile> discardedTiles() {
		return _discardedTiles;
	}

	public Seat seat() {
		return _seat;
	}
	
	protected TileRenderer tileRenderer() {
		return _gameCtrl.renderManager().tileRenderer();
	}

	public void resetTiles() {
		// Assign selectable tiles
		while (_selectableTiles.size() < GameController.PLAYER_TILE_NUM - 1) {
			_selectableTiles.add(_gameCtrl.tilePool().draw());
		}
		
		// Player seat
		int index = _gameCtrl.players().indexOf(this) - _gameCtrl.players().indexOf(_gameCtrl.userPlayer());
		_seat = SEAT_VALUES[index % SEAT_VALUES.length];
	}

	public void sortSelectableTiles(boolean enableEvent) {
		Collections.sort(_selectableTiles);
		
		for (Tile tile : _selectableTiles) {
			TileObject tileObj = tileRenderer().tileObject(tile);
			tileObj.setTransformSelectable(this, enableEvent);
			
			// Only one event will be set.
			if (enableEvent) enableEvent = false;			
		}
	}

	public void drawTile() {
		Tile tile = _gameCtrl.tilePool().draw();					assert(tile != null);
		_selectableTiles.add(tile);
	
		TileObject tileObj = tileRenderer().tileObject(tile);
		tileObj.setTransformDrawFromPool(this);
	}
	
	/**
	 * @Note The implementation should consider the flow to change state from SelectActionTile
	 * to ShowActionTile.
	 * 
	 * @return True if the implementation selected action tile, otherwise false to wait
	 * for input.
	 */
	public abstract void selectActionTile();

	protected void discardTile(Tile tile) {
		removeTileFromAllList(tile);
		_discardedTiles.add(tile);
		Collections.sort(_selectableTiles);
		
		TileObject tileObj = tileRenderer().tileObject(tile);
		tileObj.setTransformDiscarded(this);
		
		for (Tile t : _selectableTiles) {
			tileObj = tileRenderer().tileObject(t);
			tileObj.setTransformSelectable(this, false);
		}
	}

	protected void removeTileFromAllList(Tile tile) {
		if (_selectableTiles.remove(tile)) return;
		if (_discardedTiles.remove(tile)) return;
	}

}