package com.pigsar.szmj.graphic;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;
import android.util.Log;

import com.pigsar.szmj.library.Player;
import com.pigsar.szmj.library.SpecialMove;
import com.pigsar.szmj.library.Tile;

public class TileObject {
	
	enum HandTileType {
		MELDED_SPECIAL_MOVE, SELECTABLE, NEWLY_DRAWN
	}
	
	enum TileDisplay {
		MELDED, CONCEALED, CAMERA 
	}
	
	private Tile _tile;
	private float _animEndTime;
	private float _animTime;
	private boolean _animEventEnabled;
	private float[] _animStartPos = new float[3];
	private float[] _animEndPos = new float[3];
	private List<AnimationEventListener> _listeners = new ArrayList<AnimationEventListener>();
	
	// For transform functions internal use only
	private float[] _handPos = new float[3];
	private float _handAngle;
	
	public float[] transform = new float[16];
	
	
	public TileObject(Tile tile) {
		_tile = tile;
		_animTime = 0;
		_animEndTime = -1.0f;
	}
	
	public Tile tile() {
		return _tile;
	}
	
	public void addListener(AnimationEventListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}
	
	public void removeListener(AnimationEventListener listener) {
		_listeners.remove(listener);
	}
	
	public void tick(float time) {
		if (_animEndTime < 0.0f) return;
		
		_animTime += time;
		float ratio = _animTime / _animEndTime;
		if (ratio >= 1) {
			if (_animEventEnabled) {
				//Log.w("MJ - TileObject", String.format("Update! AnimEndTime = %.3f", _animEndTime));
			}
			
			ratio = 1;
			_animEndTime = -1.0f;
			
			if (_animEventEnabled) {
				for (AnimationEventListener listener : _listeners) {
					listener.onAnimationFinished(this);
				}
			}
		}
		
		transform[12] = lerp(_animStartPos[0], _animEndPos[0], ratio);
		transform[13] = lerp(_animStartPos[1], _animEndPos[1], ratio);
		transform[14] = lerp(_animStartPos[2], _animEndPos[2], ratio);
	}
	
	private static float lerp(float start, float end, float ratio) {
		return start + (end - start) * ratio;
	}
	
	public void setAnimationStart(float[] startTransform) {
		_animStartPos[0] = startTransform[12];
		_animStartPos[1] = startTransform[13];
		_animStartPos[2] = startTransform[14];
	}
	
	public void setAnimationEnd(float[] endTransform, float duration) {
		setAnimationEnd(endTransform, duration, true);
	}
	
	public void setAnimationEnd(float[] endTransform, float duration, boolean enableEvent) {
		_animTime = 0;
		_animEndTime = duration;
		_animEventEnabled = enableEvent;
		
		_animEndPos[0] = endTransform[12];
		_animEndPos[1] = endTransform[13];
		_animEndPos[2] = endTransform[14];
		
		// Make sure the transform begin at start pos
		endTransform[12] = _animStartPos[0];
		endTransform[13] = _animStartPos[1];
		endTransform[14] = _animStartPos[2];
	}
	
	public void unsetEventEnabled() {
		_animEventEnabled = false;
	}
	
	public void setTransformSelectable(Player player, boolean enableEvent) {
		setTransformSelectable(player, enableEvent, 0.1f);
	}
	
	public void setTransformSelectable(Player player, boolean enableEvent, float animationTime) {
		int index = player.selectableTiles().indexOf(tile());				assert(index >= 0);
		
		initHandValues(player, HandTileType.SELECTABLE, index);		
		
		setAnimationStart(transform);
		resetTransformFromHandValues(player, TileDisplay.CAMERA);
		setAnimationEnd(transform, 0.1f, enableEvent);
	}
	
	public void setTransformDrawFromPool(Player player) {
		int index = player.selectableTiles().indexOf(tile());				assert(index >= 0);
		initHandValues(player, HandTileType.NEWLY_DRAWN, index);
		
		// Initial transform
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, _handAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, _handPos[0], _handPos[1], _handPos[2] - 5 );
		
		setAnimationStart(transform);
		resetTransformFromHandValues(player, TileDisplay.CAMERA);
		setAnimationEnd(transform, 0.15f);
	}
	
	public void setTransformDiscarded(Player player) {
		int index = player.discardedTiles().indexOf(tile());
		if (index < 0) index = player.discardedTiles().size();
		
		float tx = TileRenderer.DISCARDED_TILE_X_START +
					((index % TileRenderer.DISCARDED_TILE_COLUMN_COUNT) *
					TileRenderer.TILE_WIDTH_RELAXED);
		float ty = TileRenderer.TILE_HEIGHT_2;
		float tz = 0;
		float handAngle = 0;
		
		if (player.seat() == Player.Seat.Bottom) {
			handAngle = 0;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_VERT;
		} else if (player.seat() == Player.Seat.Right) {
			handAngle = 90;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_HORI;
		} else if (player.seat() == Player.Seat.Top) {
			handAngle = 180;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_VERT;
		} else if (player.seat() == Player.Seat.Left) {
			handAngle = 270;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_HORI;
		}
		
		// Arrange to rows
		tz += (index/8) * TileRenderer.TILE_HEIGHT_RELAXED;
		
		setAnimationStart(transform);
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, handAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, tx, ty, tz);
		Matrix.rotateM(transform, 0, -90, 1, 0, 0);
		setAnimationEnd(transform, 0.2f);
	}
	
	public void setTransformMeldedSpecialMove(Player player) {
		// Find the special tiles index
		boolean found = false;
		int index = 0;
		int count = player.specialMoves().size();
		for (int i = 0; i < count; ++i) {
			List<Tile> moveTiles = player.specialMoves().get(i).sortedTiles(); 
			if (!moveTiles.contains(tile())) {
				index += moveTiles.size();
			} else {
				index += moveTiles.indexOf(tile());
				found = true;
				break;
			}
		}
		assert(found);
		
		initHandValues(player, HandTileType.MELDED_SPECIAL_MOVE, index);
		
		setAnimationStart(transform);
		resetTransformFromHandValues(player, TileDisplay.MELDED);
		setAnimationEnd(transform, 0.15f);
	}
	
	private void initHandValues(Player player, HandTileType type, int handTileIndex) {
		if (type == HandTileType.SELECTABLE || type == HandTileType.NEWLY_DRAWN)
		{
			// Basic pos X from special tiles
			if (player.seat() == Player.Seat.Bottom) {
				_handPos[0] = TileRenderer.USER_PLAYER_TILE_X_START;
			} else {
				_handPos[0] = TileRenderer.PLAYER_TILE_X_START;
			}
			
			_handPos[0] += handTileIndex * TileRenderer.TILE_WIDTH_RELAXED;
		
			if (type == HandTileType.NEWLY_DRAWN) {
				_handPos[0] += TileRenderer.TILE_HAND_TYPE_SPACE;
			}
		}
		else if (type == HandTileType.MELDED_SPECIAL_MOVE )
		{
			// Find tile index
			int totalSpecialMoveTileCount = 0;
			for (SpecialMove move : player.specialMoves()) {
				totalSpecialMoveTileCount += move.sortedTiles().size();
			}
			
			// Pos X for special tiles
			if (player.seat() == Player.Seat.Bottom) {
				_handPos[0] = TileRenderer.USER_PLAYER_TILE_X_MELDED_END;
			} else {
				_handPos[0] = TileRenderer.PLAYER_TILE_X_MELDED_END;
			}
			
			int i = totalSpecialMoveTileCount - handTileIndex;
			_handPos[0] -= (/*TileRenderer.TILE_WIDTH +*/ (i * TileRenderer.TILE_WIDTH_RELAXED));
		} else {
			assert(false);
		}
		
		// Other hand values
		if (player.seat() == Player.Seat.Bottom) {
			_handAngle = 0;
			if (type == HandTileType.MELDED_SPECIAL_MOVE) {
				_handPos[1] = TileRenderer.USER_PLAYER_TILE_Y_MELDED;
				_handPos[2] = TileRenderer.USER_PLAYER_TILE_Z_MELDED;
			} else {
				_handPos[1] = TileRenderer.USER_PLAYER_TILE_Y;
				_handPos[2] = TileRenderer.USER_PLAYER_TILE_Z;
			}
		} else if (player.seat() == Player.Seat.Right) {
			_handAngle = 90;
			_handPos[1] = TileRenderer.PLAYER_TILE_Y;
			_handPos[2] = TileRenderer.PLAYER_TILE_Z_HORI;
		} else if (player.seat() == Player.Seat.Top) {
			_handAngle = 180;
			_handPos[1] = TileRenderer.PLAYER_TILE_Y;
			_handPos[2] = TileRenderer.PLAYER_TILE_Z_VERT;
		} else if (player.seat() == Player.Seat.Left) {
			_handAngle = 270;
			_handPos[1] = TileRenderer.PLAYER_TILE_Y;
			_handPos[2] = TileRenderer.PLAYER_TILE_Z_HORI;
		}
	}
	
	private void resetTransformFromHandValues(Player player, TileDisplay display) {
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, _handAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, _handPos[0], _handPos[1], _handPos[2]);
		
		if (display == TileDisplay.CAMERA) {
			if (player.seat() == Player.Seat.Bottom) {
				// Set tile face camera
				Matrix.rotateM(transform, 0, -90 + TileRenderer.USER_PLAYER_TILE_ROT_X, 1, 0, 0);
			}
		} else if (display == TileDisplay.MELDED) {
			Matrix.rotateM(transform, 0, -90, 1, 0, 0);
		}
	}
	
}
