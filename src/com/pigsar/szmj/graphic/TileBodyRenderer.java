package com.pigsar.szmj.graphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.Tile;

public class TileBodyRenderer {
	
	private static float s_vertices[] = {
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,		// back
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,			// top
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,		// bottom
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,			// right
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2,		// left
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, -TileRenderer.TILE_LENGTH_2
	};
	
	private static final float TILE_FACE_TEX_NZ_LEFT	= (536.0f / 1024.0f);
	private static final float TILE_FACE_TEX_NZ_RIGHT 	= ((536.0f + 80.0f) / 1024.0f);
	private static final float TILE_FACE_TEX_NZ_TOP 	= (904.0f / 1024.0f);
	private static final float TILE_FACE_TEX_NZ_BOTTOM	= ((904.0f + 112.0f) / 1024.0f);
	private static final float TILE_FACE_TEX_PX_LEFT 	= (408.0f / 1024.0f);
	private static final float kTileFaceTexPXRight	= ((408.0f + 80.0f) / 1024.0f);
	private static final float kTileFaceTexPXTop	= (904.0f / 1024.0f);
	private static final float kTileFaceTexPXBottom	= ((904.0f + 112.0f) / 1024.0f);
	private static final float kTileFaceTexNXLeft 	= (664.0f / 1024.0f);
	private static final float kTileFaceTexNXRight 	= ((664.0f + 80.0f) / 1024.0f);
	private static final float kTileFaceTexNXTop	= (904.0f / 1024.0f);
	private static final float kTileFaceTexNXBottom	= ((904.0f + 112.0f) / 1024.0f);
	private static final float kTileFaceTexPYLeft	= (792.0f / 1024.0f);
	private static final float kTileFaceTexPYRight	= ((792.0f + 80.0f) / 1024.0f);
	private static final float kTileFaceTexPYTop	= (920.0f / 1024.0f);
	private static final float kTileFaceTexPYBottom = ((920.0f + 80.0f) / 1024.0f);
	private static final float kTileFaceTexNYLeft	= (920.0f / 1024.0f);
	private static final float kTileFaceTexNYRight	= ((920.0f + 80.0f) / 1024.0f);
	private static final float kTileFaceTexNYTop	= (920.0f / 1024.0f);
	private static final float kTileFaceTexNYBottom	= ((920.0f + 80.0f) / 1024.0f);
	
	private static float s_texture[] = {
		TILE_FACE_TEX_NZ_RIGHT, TILE_FACE_TEX_NZ_BOTTOM,
		TILE_FACE_TEX_NZ_RIGHT, TILE_FACE_TEX_NZ_TOP,
		TILE_FACE_TEX_NZ_LEFT, TILE_FACE_TEX_NZ_TOP,
		TILE_FACE_TEX_NZ_LEFT, TILE_FACE_TEX_NZ_BOTTOM,
		kTileFaceTexPYRight, kTileFaceTexPYTop,
		kTileFaceTexPYRight, kTileFaceTexPYBottom,
		kTileFaceTexPYLeft, kTileFaceTexPYBottom,
		kTileFaceTexPYLeft, kTileFaceTexPYTop,
		kTileFaceTexNYRight, kTileFaceTexNYBottom,
		kTileFaceTexNYRight, kTileFaceTexNYTop,
		kTileFaceTexNYLeft, kTileFaceTexNYTop,
		kTileFaceTexNYLeft, kTileFaceTexNYBottom,
		kTileFaceTexPXRight, kTileFaceTexPXTop,
		TILE_FACE_TEX_PX_LEFT, kTileFaceTexPXTop,
		TILE_FACE_TEX_PX_LEFT, kTileFaceTexPXBottom,
		kTileFaceTexPXRight, kTileFaceTexPXBottom,
		kTileFaceTexNXLeft, kTileFaceTexNXTop,
		kTileFaceTexNXRight, kTileFaceTexNXTop,
		kTileFaceTexNXRight, kTileFaceTexNXBottom,
		kTileFaceTexNXLeft, kTileFaceTexNXBottom
	};

	private FloatBuffer _textureBuffer;	// buffer holding the texture coordinates
	private FloatBuffer _vertexBuffer;	// buffer holding the vertices
	private int[] _textures = new int[1];
	
	private TileRenderer _tileRenderer;
	
	public TileBodyRenderer(TileRenderer tileRenderer) {
		_tileRenderer = tileRenderer;
	}
	
	public void initializeResource() {
		// Texture
		_textures[0] = TextureManager.instance().load(R.drawable.mahjong_a);
		
		// Vertex buffer
		// A float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(s_vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_vertexBuffer = byteBuffer.asFloatBuffer();			// allocates the memory from the byte buffer
		_vertexBuffer.put(s_vertices);						// fill the vertexBuffer with the vertices
		_vertexBuffer.position(0);							// set the cursor position to the beginning of the buffer
		
		// Texture buffer
		byteBuffer = ByteBuffer.allocateDirect(s_texture.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_textureBuffer = byteBuffer.asFloatBuffer();
		_textureBuffer.put(s_texture);
		_textureBuffer.position(0);
	}
	
	public void render(GL10 gl, List<Tile> tiles) {
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textures[0]);
	
		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		for (Tile tile : tiles) {
			TileObject obj = _tileRenderer.tileObject(tile);			assert(obj != null);
			
			gl.glPushMatrix();
			gl.glMultMatrixf(obj.transform, 0);
			
			// Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
			
			// Draw the vertices as triangle fan
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 4, 4);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 8, 4);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 12, 4);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 16, 4);
	
			gl.glPopMatrix();
		}
		
		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
