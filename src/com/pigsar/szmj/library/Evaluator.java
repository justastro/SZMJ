package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.pigsar.szmj.library.SpecialMove.Type;

public class Evaluator {
	
	private Player				_player;
	private List<SpecialMove>	_planningSpecialMoves = new ArrayList<SpecialMove>(5);
	
	private EnumMap<TilePattern.Type,List<Tile>> _tileTable
					= new EnumMap<TilePattern.Type,List<Tile>>(TilePattern.Type.class);
	private List<Tile>			_eyeTiles = new ArrayList<Tile>();
	private List<Boolean>		_tileCheckers = new ArrayList<Boolean>();
	private List<SpecialMove>	_passSpecialMoves = new ArrayList<SpecialMove>();
	private List<SpecialMove>	_winHandSpecialMoves = new ArrayList<SpecialMove>();
	private WinSpecialMove		_winSpecialMove;
	
	public Evaluator(Player player) {
		_player = player;
	}
	
	public List<SpecialMove> planningSpecialMoves() {
		return _planningSpecialMoves;
	}
	
	/**
	 * The returned special moves already set the player as active player of the special moves.
	 */
	public List<SpecialMove> planSelfClaimingSpecialMoves(/*Player player*/) {
		//List<SpecialMove> specialMoves = new ArrayList<SpecialMove>();
		_planningSpecialMoves.clear();
		_winSpecialMove = null;
		Tile actionTile = _player.newTile();
		
		initializeTileTable(_player.selectableTiles());
		examinateSmallKong(_planningSpecialMoves);
		examinateConcealedKong(_planningSpecialMoves);
		win(actionTile);
		specialWin(actionTile);
		
		// Set all the planning special moves with player as action player
		for (SpecialMove specialMove : _planningSpecialMoves) {
			specialMove.setRelatedPlayers(_player);
		}
		
		return _planningSpecialMoves;
	}
	
	public List<SpecialMove> planClaimingSpecialMoves(Tile actionTile, boolean isCurrentPlayer,
														boolean canChow, boolean canWin) {
		//List<SpecialMove> specialMoves = new ArrayList<SpecialMove>();
		_planningSpecialMoves.clear();
		_winSpecialMove = null;
		
		if (isCurrentPlayer) {
//			_planningSpecialMoves.add(new SpecialMove(SpecialMove.Type.GiveUp, actionTile));
		} else {
		
			initializeTileTable(_player.selectableTiles());
			if (canChow) {
				// TODO
			}
			pung(actionTile, _planningSpecialMoves);
			//bigKong(actionTile, specialMoves);
			if (canWin) {
				// TODO
			}
		}
		
		// Set all the planning special moves with player as action player
		for (SpecialMove specialMove : _planningSpecialMoves) {
			specialMove.setRelatedPlayers(_player);
		}
		
		return _planningSpecialMoves;
	}
	
	//=========================================================================
	
	private void initializeTileTable(List<Tile> tiles) {
		clearTileTable();
		for (Tile tile : tiles) {
			addTileTable(tile, false);
		}
		sortTileTable();
	}
	
	private void clearTileTable() {
		for (List<Tile> sameTypeTiles : _tileTable.values()) {
			sameTypeTiles.clear();
		}
	}
	
	private void addTileTable(Tile tile, boolean sort) {
		List<Tile> sameTypeTiles = _tileTable.get(tile.pattern().type());
		if (sameTypeTiles == null) {
			sameTypeTiles = new ArrayList<Tile>(4);
			_tileTable.put(tile.pattern().type(), sameTypeTiles);
		}
		sameTypeTiles.add(tile);

		if (sort) {
			Collections.sort(sameTypeTiles);
		}
	}
	
	private void sortTileTable() {
		for (List<Tile> sameTypeTiles : _tileTable.values()) {
			Collections.sort(sameTypeTiles);
		}
	}
	
	//=========================================================================
	
	private void pung(Tile actionTile, List<SpecialMove> outMoves) {
		if (actionTile.isBonusTile()) {
			return;
		}
		
		Tile ct1 = null;
		Tile ct2 = null;
		for (Tile tile : _tileTable.get(actionTile.pattern().type()) ) {
			if (tile.pattern().equals(actionTile.pattern())) {
				if (ct1 == null) {
					ct1 = tile;
				} else if (ct2 == null) {
					ct2 = tile;
					break;
				}
			} else if (tile.pattern().number() > actionTile.pattern().number()) {
				break;
			}
		}
		
		if (ct2 != null) {
			SpecialMove move = new SpecialMove(SpecialMove.Type.Pung, actionTile, ct1, ct2);
			outMoves.add(move);
		}
	}
	
	/**
	 * Make kong from melded pung and self action tile.
	 * @param player
	 * @param outMoves
	 */
	private void examinateSmallKong(/*Player player, */List<SpecialMove> outMoves) {
		for (SpecialMove move : _player.specialMoves()) {
			if ( move.type() == SpecialMove.Type.Pung) {
				List<Tile> tiles = _tileTable.get(move.actionTile().pattern().type());
				for (Tile tile : tiles) {
					if (tile.pattern().equals(move.actionTile())) {
						SpecialMove newMove = new SpecialMove(SpecialMove.Type.SmallKong,
																tile, move);
					}
				}
			}
		}
	}

	/**
	 * Make kong from concealed pung and self action tile.
	 * @param outMoves
	 */
	private void examinateConcealedKong(List<SpecialMove> outMoves) {
		for (List<Tile> sameTypeTiles : _tileTable.values()) {
			Tile ct1 = null;
			Tile ct2 = null;
			Tile ct3 = null;
			for (int i = 0; i < sameTypeTiles.size(); ++i) {
				Tile tile = sameTypeTiles.get(i);
				if (ct1 == null || !ct1.pattern().equals(tile.pattern())) {
					if (i + 3 < sameTypeTiles.size()) {
						ct1 = tile;			// start of possible move
					} else {
						break;				// not enough remaining tile
					}
				} else {
					if (ct2 == null) {
						ct2 = tile;
					} else if (ct3 == null) {
						ct3 = tile;
					} else {
						SpecialMove newMove = new SpecialMove(SpecialMove.Type.ConcealedKong,
																tile, ct1, ct2, ct3);
						outMoves.add(newMove);
					}
				}
			}
		}
	}
	
	private void win(/*Player player, */Tile actionTile) {
		SpecialMove eyeSpecialMove = null;
		boolean haveEye = false;
		
		_winHandSpecialMoves.clear();
		
		for (List<Tile> sameTypeTiles : _tileTable.values()) {
			int remainder = sameTypeTiles.size() % 3;
			if (remainder == 1) {
				return;
			} else if (remainder == 0) {
				// All are pung and/or chow if completed
				resetTileCheckers(sameTypeTiles.size());
				if (!judgeCompleted(sameTypeTiles)) {
					return;
				} else {
					_winHandSpecialMoves.addAll(_passSpecialMoves);
				}
			} else if (remainder == 2) {
				if (haveEye || !resetEyeTiles(sameTypeTiles)) {
					return;
				}
				
				for (Tile eyeTile : _eyeTiles) {
					eyeSpecialMove = resetTileCheckers(sameTypeTiles, eyeTile);
					if (judgeCompleted(sameTypeTiles)) {
						_winHandSpecialMoves.addAll(_passSpecialMoves);
						haveEye = true;
						break;
					}
				}
				
				if (!haveEye) {
					return;
				}
			}
		}
		
		//if (eyeSpecialMove != null)							// TODO: problem??
			_winHandSpecialMoves.add(eyeSpecialMove);
		//}
			
		Collections.reverse(_player.specialMoves());				// TODO: problem??
		_winHandSpecialMoves.addAll(_player.specialMoves());
		
		//mahjongPoints(player);
	}
	
	private void specialWin(/*Player player, */Tile actionTile) {
		handThirteenOrphans(/*player, */actionTile);
	}
	
	private void resetTileCheckers(int minimumSize) {
		// Reset existing checkers
		for (int i = 0; i < _tileCheckers.size(); ++i) {
			_tileCheckers.set(i, Boolean.FALSE);
		}
		
		// Ensure minimum size
		while (_tileCheckers.size() < minimumSize) {
			_tileCheckers.add(Boolean.FALSE);
		}
	}
	
	private SpecialMove resetTileCheckers(List<Tile> tiles, Tile eyeTile) {
		resetTileCheckers(tiles.size());
		
		// Check the tiles same as the eye, otherwise uncheck.
		Tile eye1 = null;
		Tile eye2 = null;
		for (int i = 0; i < tiles.size() && eye2 == null; ++i) {
			Tile tile = tiles.get(i);
			if (tile.pattern().equals(eyeTile.pattern())) {
				_tileCheckers.set(i, Boolean.TRUE);
				if (eye1 == null) {
					eye1 = tile;
				} else if (eye2 == null) {
					eye2 = tile;
				}
			}
		}
		
		// TODO: Correct??
		return new SpecialMove(SpecialMove.Type.Eyes, null, eye1, eye2);
	}
	
	/**
	 * From: processEyes();
	 */
	private boolean resetEyeTiles(List<Tile> tiles) {
		_eyeTiles.clear();
		
		Tile eyeTile = null;
		boolean added = false;
		for (Tile tile : tiles) {
			if (tile.isBonusTile()) {
				continue;
			}
			
			if (eyeTile == null || !tile.pattern().equals(eyeTile.pattern())) {
				// Start of possible eye series
				eyeTile = tile;
				added = false;
			} else if (!added) {
				_eyeTiles.add(tile);
				added = true;
			}
		}
		
		return _eyeTiles.size() > 0;
	}
	
	/**
	 * From: isCompleted()
	 */
	private boolean judgeCompleted(List<Tile> tiles) {
		_passSpecialMoves.clear();
		
		for (int i = 0; i < tiles.size(); ++i) {
			if (!_tileCheckers.get(i).booleanValue()) {
				if (!markCheckerForPung(tiles, i) && !markCheckerForChow(tiles, i)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean markCheckerForPung(List<Tile> tiles, int index) {
		if (_tileCheckers.get(index).booleanValue()) {
			return false;
		}
		
		Tile tile = tiles.get(index);
		if (tile.isBonusTile()) {
			return false;
		}
		
		int secondIndex = -1;
		for (int i = index + 1; i < tiles.size(); ++i) {
			Tile near = tiles.get(i);
			if (!near.pattern().equals(tile.pattern())) {
				// Non-consecutive must not pung
				return false;
			} else if (!_tileCheckers.get(i).booleanValue()) {
				if (secondIndex < 0) {
					// Found the second
					secondIndex = i;
				} else {
					// Found the third
					_tileCheckers.set(index, Boolean.TRUE);
					_tileCheckers.set(secondIndex, Boolean.TRUE);
					_tileCheckers.set(i, Boolean.TRUE);
					
					_passSpecialMoves.add(new SpecialMove(SpecialMove.Type.ClosedPung, null,
							tiles.get(index), tiles.get(secondIndex), tiles.get(i)));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean markCheckerForChow(List<Tile> tiles, int index) {
		if (_tileCheckers.get(index).booleanValue()) {
			return false;
		}
		
		// Chow never consist by bonus tile
		Tile tile = tiles.get(index);
		if (tile.isBonusTile()) {
			return false;
		}
		
		int secondIndex = -1;
		for (int i = index + 1; i < tiles.size(); ++i) {
			Tile near = tiles.get(i);
			if (near.pattern().type() != tile.pattern().type()) {
				return false;
			}
			
			if (near.pattern().number() == tile.pattern().number()) {
				// Skip the same pattern consective tile
				continue;
			} else if (near.pattern().number() - tile.pattern().number() == 1) {
				if (secondIndex == -1) {
					continue;
				} else if (!_tileCheckers.get(i).booleanValue()) {
					// Found second
					secondIndex = i;
				}
			} else if (near.pattern().number() - tile.pattern().number() == 2) {
				if (secondIndex == -1) {
					return false;
				} else if (!_tileCheckers.get(i).booleanValue()) {
					// Found third
					_tileCheckers.set(index, Boolean.TRUE);
					_tileCheckers.set(secondIndex, Boolean.TRUE);
					_tileCheckers.set(i, Boolean.TRUE);
					
					_passSpecialMoves.add(new SpecialMove(SpecialMove.Type.ClosedChow, null,
							tiles.get(index), tiles.get(secondIndex), tiles.get(i)));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	//=========================================================================
	
	void handSevenPairs(Player player, Tile actionTile) {
		
	}
	
	void handThirteenOrphans(/*Player player, */Tile actionTile) {
		if (_player.specialMoves().size() > 0) {
			return;
		}
		
		boolean assumeContainEye = false;
		for (TilePattern.Type type : _tileTable.keySet()) {
			List<Tile> sameTypeTiles = _tileTable.get(type);
			
			if (type == TilePattern.Type.Honor) {
				// Tile count check
				if (sameTypeTiles.size() == 8) {
					if (assumeContainEye) {
						return;
					}
					assumeContainEye = true;
				} else if (sameTypeTiles.size() != 7) {
					return;
				}
				
				// Tile number contains all 7 types of number, regardless eye or not.
				int checkNumber = 0;
				for (Tile tile : sameTypeTiles) {		// TODO: sameTypeTiles must in number order??
					if (tile.pattern().number() == checkNumber) {
						++checkNumber;
					}
				}
				if (checkNumber != 7) {
					return;
				}
			} else {
				// Tile count check
				if (sameTypeTiles.size() == 3) {
					if (assumeContainEye) {
						return;
					}
					assumeContainEye = true;
				} else if (sameTypeTiles.size() != 2) {
					return;
				}
				
				// Tile number contains 1 or 9 only
				boolean check1 = false;
				boolean check9 = false;
				for (Tile tile : sameTypeTiles) {
					if (tile.pattern().number() == 1) {
						check1 = true;
					} else if (tile.pattern().number() == 9) {
						check9 = true;
					} else {
						return;
					}
				}
				if (!check1 || !check9) {
					return;
				}
			}
		}
		
		// Must contains eye finally
		if (!assumeContainEye) {
			return;
		}
		
		if (_winSpecialMove == null) {
			_winSpecialMove = new WinSpecialMove(Type.SuperWin, actionTile);
		}
		_winSpecialMove.addPoint(WinSpecialMove.PointType.ThirteenOrphans, 1);
	}
	
	//=========================================================================
	
	
}


