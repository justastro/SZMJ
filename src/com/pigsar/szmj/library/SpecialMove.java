package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecialMove {
	
	public enum Type {
		Invalid,
		ClosedChow,
		ClosedPung,
		Eyes,
		GiveUp,
		Chow,
		Pung,
		BigKong,
		SmallKong,
		ConcealedKong,
		Win,
		SuperWin,
	}
	
	private Type _type;
	private Tile _actionTile;
	private List<Tile> _concealedTiles = new ArrayList<Tile>();
	private List<Tile> _sortedTiles = new ArrayList<Tile>();
	private Player _actionPlayer;
//	private Player _specialMovePlayer;
	
	public SpecialMove(Type type, Tile action) {
		_type = type;
		_actionTile = action;
		updateSortedTiles();
	}
	
	public SpecialMove(Type type, Tile action, SpecialMove move) {
		_type = type;
		_actionTile = action;
		_concealedTiles.add(move.actionTile());
		_concealedTiles.addAll(move.concealedTiles());
		updateSortedTiles();
	}
	
	public SpecialMove(Type type, Tile action, Tile concealed1, Tile concealed2) {
		_type = type;
		_actionTile = action;
		_concealedTiles.add(concealed1);
		_concealedTiles.add(concealed2);
		updateSortedTiles();
	}
	
	public SpecialMove(Type type, Tile action, Tile concealed1, Tile concealed2, Tile concealed3) {
		_type = type;
		_actionTile = action;
		_concealedTiles.add(concealed1);
		_concealedTiles.add(concealed2);
		_concealedTiles.add(concealed3);
		updateSortedTiles();
	}
	
	public Type type() {
		return _type;
	}
	
	public Tile actionTile() {
		return _actionTile;
	}
	
	public List<Tile> concealedTiles() {
		return _concealedTiles;
	}
	
	public Player actionPlayer() {
		return _actionPlayer;
	}
	
//	public Player specialMovePlayer() {
//		return _specialMovePlayer;
//	}
	
	public void setRelatedPlayers(Player actionAndSpecialMovePlayer) {
		setRelatedPlayers(actionAndSpecialMovePlayer, actionAndSpecialMovePlayer);
	}
	
	public void setRelatedPlayers(Player actionPlayer, Player specialMovePlayer) {
		_actionPlayer = actionPlayer;
//		_specialMovePlayer = specialMovePlayer;
	}
	
	private void updateSortedTiles() {
		_sortedTiles.clear();
		if (_actionTile != null) {
			_sortedTiles.add(_actionTile);
		}
		_sortedTiles.addAll(_concealedTiles);
		Collections.sort(_sortedTiles);
	}
	
	public List<Tile> sortedTiles() {
		return _sortedTiles;
	}
}
