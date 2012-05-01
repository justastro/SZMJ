package com.pigsar.szmj.library;

import java.util.ArrayList;

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
	private ArrayList<Tile> _concealedTiles = new ArrayList<Tile>();
	private AbstractPlayer _actionPlayer;
	private AbstractPlayer _specialMovePlayer;
	
	public SpecialMove(Type type, Tile action) {
		_type = type;
		_actionTile = action;
	}
	
	public SpecialMove(Type type, Tile action, Tile concealed1, Tile concealed2) {
		_type = type;
		_actionTile = action;
		_concealedTiles.add(concealed1);
		_concealedTiles.add(concealed2);
	}
	
	public SpecialMove(Type type, Tile action, Tile concealed1, Tile concealed2, Tile concealed3) {
		_type = type;
		_actionTile = action;
		_concealedTiles.add(concealed1);
		_concealedTiles.add(concealed2);
		_concealedTiles.add(concealed3);
	}
}
