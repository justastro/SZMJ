package com.pigsar.szmj.graphic;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;
import android.util.Log;

import com.pigsar.szmj.library.AbstractPlayer;
import com.pigsar.szmj.library.Tile;
import com.pigsar.szmj.library.UserPlayer;

public class TileObject {
	
	private Tile _tile;
	private float _animEndTime;
	private float _animTime;
	private boolean _animEventEnabled;
	private float[] _animStartPos = new float[3];
	private float[] _animEndPos = new float[3];
	private List<AnimationEventListener> _listeners = new ArrayList<AnimationEventListener>();
	
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
				Log.w("MJ - TileObject", String.format("Update! AnimEndTime = %.3f", _animEndTime));
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
		return start + (end-start) * ratio;
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
	
	public void setTransformSelectable(AbstractPlayer player, boolean enableEvent) {
		setTransformSelectable(player, enableEvent, 0.1f);
	}
	
	public void setTransformSelectable(AbstractPlayer player, boolean enableEvent, float animationTime) {
		int index = player.selectableTiles().indexOf(tile());				assert(index >= 0);
		float tx = TileRenderer.USER_PLAYER_TILE_X_START + (index * TileRenderer.TILE_WIDTH_RELAXED);
		float ty = TileRenderer.TILE_HEIGHT_2;
		float tz = 0;
		float seatAngle = 0;
		
		//if (index == player.selectableTiles().size() - 1) {
		//	tx += TileRenderer.TILE_NEWLY_DRAWED_OFFSET;
		//}
		
		if (player.seat() == AbstractPlayer.Seat.Bottom) {
			seatAngle = 0;
			tz = TileRenderer.USER_PLAYER_TILE_Z;
			ty = TileRenderer.USER_PLAYER_TILE_Y;
		} else if (player.seat() == AbstractPlayer.Seat.Right) {
			seatAngle = 90;
			tz = TileRenderer.PLAYER_TILE_Z_HORI;
		} else if (player.seat() == AbstractPlayer.Seat.Top) {
			seatAngle = 180;
			tz = TileRenderer.PLAYER_TILE_Z_VERT;
		} else if (player.seat() == AbstractPlayer.Seat.Left) {
			seatAngle = 270;
			tz = TileRenderer.PLAYER_TILE_Z_HORI;
		}
		
		setAnimationStart(transform);
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, seatAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, tx, ty, tz);
		//if (player.seat() == AbstractPlayer.Seat.Bottom) {
		if (player instanceof UserPlayer) {
			// For user player show tiles facing camera
			Matrix.rotateM(transform, 0, -90 + TileRenderer.USER_PLAYER_TILE_ROT_X, 1, 0, 0);
		}
		setAnimationEnd(transform, 0.1f, enableEvent);
	}
	
	public void setTransformDrawFromPool(AbstractPlayer player) {
		int index = player.selectableTiles().indexOf(tile());				assert(index >= 0);
		float tx = TileRenderer.USER_PLAYER_TILE_X_START + (index * TileRenderer.TILE_WIDTH_RELAXED) +
				   TileRenderer.TILE_NEWLY_DRAWED_OFFSET;
		float ty = TileRenderer.TILE_HEIGHT_2;
		float tz = 0;
		float seatAngle = 0;
		
		if (player.seat() == AbstractPlayer.Seat.Bottom) {
			seatAngle = 0;
			tz = TileRenderer.USER_PLAYER_TILE_Z;
			ty = TileRenderer.USER_PLAYER_TILE_Y;
		} else if (player.seat() == AbstractPlayer.Seat.Right) {
			seatAngle = 90;
			tz = TileRenderer.PLAYER_TILE_Z_HORI;
		} else if (player.seat() == AbstractPlayer.Seat.Top) {
			seatAngle = 180;
			tz = TileRenderer.PLAYER_TILE_Z_VERT;
		} else if (player.seat() == AbstractPlayer.Seat.Left) {
			seatAngle = 270;
			tz = TileRenderer.PLAYER_TILE_Z_HORI;
		}
		
		// TEMP initial transform
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, seatAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, tx, ty, tz - 5 );
		
		setAnimationStart(transform);
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, seatAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, tx, ty, tz);
		if (player.seat() == AbstractPlayer.Seat.Bottom) {
			// For user player show tiles facing camera
			Matrix.rotateM(transform, 0, -90 + TileRenderer.USER_PLAYER_TILE_ROT_X, 1, 0, 0);
		}
		setAnimationEnd(transform, 0.15f);
	}
	
	public void setTransformDiscarded(AbstractPlayer player) {
		int index = player.discardedTiles().indexOf(tile());
		if (index < 0) index = player.discardedTiles().size();
		
		float tx = TileRenderer.DISCARDED_TILE_X_START +
					((index % TileRenderer.DISCARDED_TILE_COLUMN_COUNT) *
					TileRenderer.TILE_WIDTH_RELAXED);
		float ty = TileRenderer.TILE_HEIGHT_2;
		float tz = 0;
		float seatAngle = 0;
		
		if (player.seat() == AbstractPlayer.Seat.Bottom) {
			seatAngle = 0;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_VERT;
		} else if (player.seat() == AbstractPlayer.Seat.Right) {
			seatAngle = 90;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_HORI;
		} else if (player.seat() == AbstractPlayer.Seat.Top) {
			seatAngle = 180;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_VERT;
		} else if (player.seat() == AbstractPlayer.Seat.Left) {
			seatAngle = 270;
			tz = TileRenderer.DISCARDED_TILE_CENTER_OFFSET_Z_HORI;
		}
		
		tz += (index/8) * TileRenderer.TILE_HEIGHT_RELAXED;
		
		setAnimationStart(transform);
		Matrix.setIdentityM(transform, 0);
		Matrix.rotateM(transform, 0, seatAngle, 0, 1, 0);
		Matrix.translateM(transform, 0, tx, ty, tz);
		Matrix.rotateM(transform, 0, -90, 1, 0, 0);
		setAnimationEnd(transform, 0.2f);
	}
}
