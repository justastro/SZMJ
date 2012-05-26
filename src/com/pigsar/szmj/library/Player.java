package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pigsar.szmj.graphic.TileObject;
import com.pigsar.szmj.graphic.TileRenderer;

public abstract class Player {

	public static final Seat[] SEAT_VALUES = Seat.values();

	public enum Seat {
		Bottom, Right, Top, Left
	}

	protected GameController _gameCtrl;
	protected Evaluator _evaluator = new Evaluator();
	
	protected List<Tile> _tiles = new ArrayList<Tile>(38);
	protected List<Tile> _selectableTiles = new ArrayList<Tile>(14);
	protected List<Tile> _discardedTiles = new ArrayList<Tile>(38);
	protected List<SpecialMove> _specialMoves = new ArrayList<SpecialMove>(5);
	protected List<SpecialMove> _availableSpecialMoves = new ArrayList<SpecialMove>(5);
	protected SpecialMove _selectedSpecialMove;
	protected Seat _seat;

	public Player(GameController controller) {
		_gameCtrl = controller;
	}
	
	public Evaluator evaluator() {
		return _evaluator;
	}

	public List<Tile> tiles() {
		_tiles.clear();
		_tiles.addAll(_selectableTiles);
		_tiles.addAll(_discardedTiles);
		return _tiles;
	}

	public List<Tile> selectableTiles() {
		return _selectableTiles;
	}

	public List<Tile> discardedTiles() {
		return _discardedTiles;
	}
	
	public List<SpecialMove> specialMoves() {
		return _specialMoves;
	}
	
//	public void clearAvailableSpecialMove() {
//		_availableSpecialMoves.clear();
//	}
	
	public SpecialMove selectedSpecialMove() {
		return _selectedSpecialMove;
	}
	
	public void setSelectedSpecialMove(SpecialMove specialMove) {
		_selectedSpecialMove = specialMove;
	}
	
	public Tile newTile() {
		int n = _selectableTiles.size();
		if (n % 3 == 2) {
			return _selectableTiles.get(n - 1);
		}
		return null;
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
		// Sort without the new tile
		Tile newTile = newTile();
		_selectableTiles.remove(newTile);
		Collections.sort(_selectableTiles);
		_selectableTiles.add(newTile);
		
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
	 */
	public abstract void selectActionTile();

	protected void discardTile(Tile tile) {
		removeTileFromAllList(tile);
		_discardedTiles.add(tile);
		Collections.sort(_selectableTiles);
		
		_gameCtrl.setActionTile(tile);
		
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

	//=========================================================================
	
	public List<SpecialMove> genereateAvailableSpecialMoves() {
		_availableSpecialMoves.clear();
		
		_gameCtrl.evaluator().checkSpecialMoves(this, )
		
		return _availableSpecialMoves;
	}
}