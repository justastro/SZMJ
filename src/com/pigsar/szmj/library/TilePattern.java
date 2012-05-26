package com.pigsar.szmj.library;


/***
 * A pattern represents a unique identification in a tile, but not include
 * repeated duplication. For example, there exist only one Bamboo[1] pattern,
 * shared by four tiles in a pool.
 * 
 * @author justinleung
 *
 */
public class TilePattern implements Comparable<TilePattern> {

	public enum Type {
		Character,
		Dot,
		Bamboo,
		Honor,
		Flower,
	}
	
	public enum HonorNumber {
		East,
		South,
		West,
		North,
		Red,
		Green,
		WhiteDrgaon
	}
	
	public enum FlowerNumber {
		WeatherSpring,
		WeatherSummer,
		WeatherAutumn,
		WeatherWinter,
		PlantPlum,
		PlantOrchid,
		PlantChrysanthemum,
		PlantBamboo,
	}
	
	
	private Type _type;
	private int _number;
	private int _textureIndex;
	
	TilePattern(Type type, int number, int textureIndex) {
		_type = type;
		_number = number;
		_textureIndex = textureIndex;
	}
	
	public Type type() {
		return _type;
	}
	
	public int number() {
		return _number;
	}
	
	public int textureIndex() {
		return _textureIndex;
	}
	
	public String toString() {
		if (_type == Type.Honor) {
			return _type.toString() + HonorNumber.values()[_number].toString();
		} else if (_type == Type.Flower) {
			return _type.toString() + FlowerNumber.values()[_number].toString();
		} else {
			return _type.toString() + String.valueOf(_number);
		}
	}

	public int compareTo(TilePattern another) {
		int result = type().compareTo(another.type());
		if (result == 0) {
			if (number() < another.number()) {
				result = -1;
			} else if (number() > another.number()) {
				result = 1;
			}
		}
		return result;
	}
	
	public boolean equals(TilePattern another) {
		return (compareTo(another) == 0);
	}
	
}




