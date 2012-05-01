package com.pigsar.szmj.graphic.old;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.R;
import com.pigsar.szmj.graphic.TextureManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class TileFace implements Renderable {
	
	private static final float kTileWidth = 3.0f;
	private static final float kTileWidth2 = kTileWidth/2;
	private static final float kTileHeight = 4.2f;
	private static final float kTileHeight2 = kTileHeight/2;
	private static final float kTileLength = 2.0f;
	private static final float kTileLength2 = kTileLength/2;
	
	private static float s_vertices[] = {
		kTileWidth2, -kTileHeight2, kTileLength2,
		kTileWidth2, kTileHeight2, kTileLength2,
		-kTileWidth2, kTileHeight2, kTileLength2,
		-kTileWidth2, -kTileHeight2, kTileLength2
	};
	
	private static final float kTileTextureSize		= 1024.0f;
	private static final float kTileTextureLeft		= 18.0f;
	private static final float kTileTextureTop		= 2.0f;
	private static final float kTileTextureWidth	= 92.0f;
	private static final float kTileTextureHeight	= 124.0f;
	private static final float kTileFaceTexLeft		= (kTileTextureLeft / kTileTextureSize);
	private static final float kTileFaceTexRight	= ((kTileTextureLeft + kTileTextureWidth) / kTileTextureSize);
	private static final float kTileFaceTexTop		= (kTileTextureTop / kTileTextureSize);
	private static final float kTileFaceTexBottom	= ((kTileTextureTop + kTileTextureHeight) / kTileTextureSize);
	
	private static float s_texture[] = {
		kTileFaceTexRight, kTileFaceTexBottom,
		kTileFaceTexRight, kTileFaceTexTop,
		kTileFaceTexLeft, kTileFaceTexTop,
		kTileFaceTexLeft, kTileFaceTexBottom
	};

	private FloatBuffer _textureBuffer;	// buffer holding the texture coordinates
	private FloatBuffer _vertexBuffer;	// buffer holding the vertices
	private int[] _textures = new int[1];
	private int _player;
	private int _posIndex;
	private int _patternIndex;
	
	public TileFace(int player, int posIndex, int patternIndex) {
		_player = player;
		_posIndex = posIndex;
		_patternIndex = patternIndex;
		
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(s_vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_vertexBuffer = byteBuffer.asFloatBuffer();			// allocates the memory from the byte buffer
		_vertexBuffer.put(s_vertices);						// fill the vertexBuffer with the vertices
		_vertexBuffer.position(0);							// set the cursor position to the beginning of the buffer
		
		final float paddingX = (_patternIndex % 8) * (1.0f / 8.0f);
		final float patternY = (_patternIndex / 8) * (1.0f / 8.0f);
		float[] textures = {
			paddingX + kTileFaceTexRight, patternY + kTileFaceTexBottom,
			paddingX + kTileFaceTexRight, patternY + kTileFaceTexTop,
			paddingX + kTileFaceTexLeft, patternY + kTileFaceTexTop,
			paddingX + kTileFaceTexLeft, patternY + kTileFaceTexBottom
		};
		
		byteBuffer = ByteBuffer.allocateDirect(textures.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_textureBuffer = byteBuffer.asFloatBuffer();
		_textureBuffer.put(textures);
		_textureBuffer.position(0);
	}
	
	public void loadGLTexture(GL10 gl, Context context) {
		_textures[0] = TextureManager.instance().load(R.drawable.mahjong_a);
	}

	public void draw(GL10 gl) {
		float distance = (_player == 1) ? -30.0f : (_player == 3 ) ? 0f: -33.0f;
		gl.glPushMatrix();
		if (_player == 0) {
			gl.glRotatef(90, 0, 1, 0);
		} else if (_player == 2) {
			gl.glRotatef(-90, 0, 1, 0);
		} else if (_player == 3) {
			gl.glRotatef(-180, 0, 1, 0);
		}
		gl.glTranslatef(-5.5f * kTileWidth + _posIndex * kTileWidth, 0.0f, distance);
		gl.glRotatef(-180, 0, 1, 0);					// tile back to the front
		
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textures[0]);
		
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
		
		// Draw the vertices as triangle fan
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glPopMatrix();
	}

}
