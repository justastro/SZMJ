package com.pigsar.szmj.library;

import java.util.ArrayList;
import java.util.Collections;

public class TilePool {
	
	ArrayList<Tile> _tiles = new ArrayList<Tile>();
	ArrayList<Tile> _availableTiles = new ArrayList<Tile>();
	int _startGameScore;
	int _startHandScore;
	int _score;
	
	public TilePool() {
		reset();
	}
	
	public ArrayList<Tile> availableTiles() {
		return _availableTiles;
	}

	public void reset() {
		_tiles.clear();
		_availableTiles.clear();
		
		for (int p = 0; p < 9; ++p) {
			// Create patterns
			TilePattern bambooPattern = new TilePattern(TilePattern.Type.Bamboo, p+1, p+18);
			TilePattern charPattern = new TilePattern(TilePattern.Type.Character, p+1, p+9);
			TilePattern dotPattern = new TilePattern(TilePattern.Type.Dot, p+1, p);
			
			TilePattern honorPattern = null;
			if (p < TilePattern.HonorNumber.values().length) {		// should be 7
				honorPattern = new TilePattern(TilePattern.Type.Honor, p, p+27);
			}
			TilePattern flowerPattern = null;
			if (p < TilePattern.FlowerNumber.values().length) {		// should be 2 * 4
				flowerPattern = new TilePattern(TilePattern.Type.Flower, p, p+34);
			}
		
			// Create tiles by patterns
			for (int i = 0; i < 4; ++i) {
				_tiles.add(new Tile(bambooPattern, i));
				_tiles.add(new Tile(charPattern, i));
				_tiles.add(new Tile(dotPattern, i));
				if (honorPattern != null) _tiles.add(new Tile(honorPattern, i));
			}
			if (flowerPattern != null) _tiles.add(new Tile(flowerPattern, 0));
		}
		
		_availableTiles.addAll(_tiles);
		shuffle();
	}
	
	public void shuffle() {
		Collections.shuffle(_availableTiles);
	}
	
	public boolean isValid() {
		return !_availableTiles.isEmpty();
	}
	
	public Tile draw() {
		int size = _availableTiles.size();
		if (size > 0) {
			return _availableTiles.remove(size - 1);
		} else {
			return null;
		}
	}
}
