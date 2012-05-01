package com.pigsar.szmj.library;

public class Tile implements Comparable<Tile> {

	private TilePattern _pattern;
	private int _index;
	//private boolean _changed;
	
	public Tile(TilePattern pattern, int index) {
		_pattern = pattern;
		_index = index;
	}
	
	public TilePattern pattern() {
		return _pattern;
	}
	
	public int index() {
		return _index;
	}

	public int compareTo(Tile another) {
		return pattern().compareTo(another.pattern());
	}
	
//	public boolean isChanged() {
//		return _changed;
//	}
//	
//	public void setChanged( boolean changed ) {
//		_changed = changed;
//	}
}
