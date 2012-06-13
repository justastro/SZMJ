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
	protected Evaluator _evaluator;
	
	protected List<Tile> _tiles = new ArrayList<Tile>(38);
	protected List<Tile> _selectableTiles = new ArrayList<Tile>(14);
	protected List<Tile> _discardedTiles = new ArrayList<Tile>(38);
	protected List<SpecialMove> _specialMoves = new ArrayList<SpecialMove>(5);
	//protected List<SpecialMove> _availableSpecialMoves = new ArrayList<SpecialMove>(5);
	protected SpecialMove _plannedSpecialMove;
	protected Seat _seat;

	public Player(GameController controller) {
		_gameCtrl = controller;
		_evaluator = new Evaluator(this);
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
	
	public SpecialMove plannedSpecialMove() {
		return _plannedSpecialMove;
	}
	
	public boolean isCurrentPlayer() {
		return (_gameCtrl.currentPlayer() == this);
	}
	
	public boolean isNextPlayer() {
		return (_gameCtrl.nextPlayer() == this);
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
		while (_selectableTiles.size() < GameController.PLAYER_TILE_NUM) {
		//while (_selectableTiles.size() < GameController.PLAYER_TILE_NUM - 10) {
			_selectableTiles.add(_gameCtrl.tilePool().draw());
		}
		
		// Player seat
		int userPlayerIndex = _gameCtrl.players().indexOf(_gameCtrl.userPlayer());
		int index = _gameCtrl.players().indexOf(this) - userPlayerIndex;
		_seat = SEAT_VALUES[index % SEAT_VALUES.length];
		
//		//======= SPECIAL MOVE TEST =======
//		SpecialMove sp = new SpecialMove(SpecialMove.Type.Pung, _gameCtrl.tilePool().draw(),
//				_gameCtrl.tilePool().draw(), _gameCtrl.tilePool().draw());
//		_specialMoves.add(sp);
//		sp = new SpecialMove(SpecialMove.Type.Pung, _gameCtrl.tilePool().draw(),
//				_gameCtrl.tilePool().draw(), _gameCtrl.tilePool().draw());
//		_specialMoves.add(sp);
	}

	public void sortSelectableTiles(boolean enableEvent) {
		// Sort without the new tile
		Tile newTile = newTile();
		if (newTile != null) {
			_selectableTiles.remove(newTile);
		}
		Collections.sort(_selectableTiles);
		if (newTile != null) {
			_selectableTiles.add(newTile);
		}
		
		for (Tile tile : _selectableTiles) {
			TileObject tileObj = tileRenderer().tileObject(tile);
			tileObj.setTransformSelectable(this, enableEvent);
			
			// Only one event will be set.
			if (enableEvent) enableEvent = false;
		}
		
//		// TEST FOR SPECIAL MOVES
//		for (SpecialMove move : _specialMoves) {
//			for (Tile tile : move.sortedTiles()) {
//				TileObject tileObj = tileRenderer().tileObject(tile);
//				tileObj.setTransformMeldedSpecialMove(this);
//			}
//		}
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
	
//	public List<SpecialMove> genereateAvailableSpecialMoves() {
//		_availableSpecialMoves.clear();
//		
//		_gameCtrl.evaluator().checkSpecialMoves(this, )
//		
//		return _availableSpecialMoves;
//	}
	
	public boolean planSelfClaimingSpecialMoves() {
		return !_evaluator.planSelfClaimingSpecialMoves().isEmpty();
	}
	
//	public List<SpecialMove> claimSpecialMoves(Tile actionTile, boolean canChow, boolean canWin) {
//		return _evaluator.checkClaimSpecialMoves(actionTile, canChow, canWin);
//	}
	
	public boolean planClaimingSpecialMoves(Tile actionTile) {
		boolean canChow = isNextPlayer();
		boolean canWin = false;
		return !_evaluator.planClaimingSpecialMoves(actionTile, isCurrentPlayer(), canChow,
														canWin).isEmpty();
	}

	/**
	 * @Note Implementation must consider the flow to ShowSpecialMoveLabel
	 */
	public abstract void selectSpecialMove();
	
	public void claimPlannedSpecialMove() {
		// Update the ownership of tiles in the special move
		SpecialMove move = plannedSpecialMove();
		move.actionPlayer().removeTileFromAllList(move.actionTile());
		for (Tile tile : move.concealedTiles()) {
			removeTileFromAllList(tile);
		}
		
		_specialMoves.add(plannedSpecialMove());
		
		for (Tile tile : plannedSpecialMove().sortedTiles()) {
			TileObject tileObj = tileRenderer().tileObject(tile);
			tileObj.setTransformMeldedSpecialMove(this);
		}
		
		sortSelectableTiles(true);
	}
	
}