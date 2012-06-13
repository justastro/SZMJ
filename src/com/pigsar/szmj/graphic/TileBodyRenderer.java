package com.pigsar.szmj.graphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.Tile;

public class TileBodyRenderer {
	
	private static float VERTICES[] = {
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
	
	private static final float TEX_NZ_LEFT		= (536.0f / 1024.0f);
	private static final float TEX_NZ_RIGHT 	= ((536.0f + 80.0f) / 1024.0f);
	private static final float TEX_NZ_TOP 		= (904.0f / 1024.0f);
	private static final float TEX_NZ_BOTTOM	= ((904.0f + 112.0f) / 1024.0f);
	private static final float TEX_PX_LEFT 		= (408.0f / 1024.0f);
	private static final float TEX_PX_RIGHT		= ((408.0f + 80.0f) / 1024.0f);
	private static final float TEX_PX_TOP		= (904.0f / 1024.0f);
	private static final float TEX_PX_BOTTOM	= ((904.0f + 112.0f) / 1024.0f);
	private static final float TEX_NX_LEFT 		= (664.0f / 1024.0f);
	private static final float TEX_NX_RIGHT 	= ((664.0f + 80.0f) / 1024.0f);
	private static final float TEX_NX_TOP		= (904.0f / 1024.0f);
	private static final float TEX_NX_BOTTOM	= ((904.0f + 112.0f) / 1024.0f);
	private static final float TEX_PY_LEFT		= (792.0f / 1024.0f);
	private static final float TEX_PY_RIGHT		= ((792.0f + 80.0f) / 1024.0f);
	private static final float TEX_PY_TOP		= (920.0f / 1024.0f);
	private static final float TEX_PY_BOTTOM	= ((920.0f + 80.0f) / 1024.0f);
	private static final float TEX_NY_LEFT		= (920.0f / 1024.0f);
	private static final float TEX_NY_RIGHT		= ((920.0f + 80.0f) / 1024.0f);
	private static final float TEX_NY_TOP		= (920.0f / 1024.0f);
	private static final float TEX_NY_BOTTOM	= ((920.0f + 80.0f) / 1024.0f);
	
	private static final float TEX_COORDS[] = {
		TEX_NZ_RIGHT, TEX_NZ_BOTTOM,
		TEX_NZ_RIGHT, TEX_NZ_TOP,
		TEX_NZ_LEFT, TEX_NZ_TOP,
		TEX_NZ_LEFT, TEX_NZ_BOTTOM,
		TEX_PY_RIGHT, TEX_PY_TOP,
		TEX_PY_RIGHT, TEX_PY_BOTTOM,
		TEX_PY_LEFT, TEX_PY_BOTTOM,
		TEX_PY_LEFT, TEX_PY_TOP,
		TEX_NY_RIGHT, TEX_NY_BOTTOM,
		TEX_NY_RIGHT, TEX_NY_TOP,
		TEX_NY_LEFT, TEX_NY_TOP,
		TEX_NY_LEFT, TEX_NY_BOTTOM,
		TEX_PX_RIGHT, TEX_PX_TOP,
		TEX_PX_LEFT, TEX_PX_TOP,
		TEX_PX_LEFT, TEX_PX_BOTTOM,
		TEX_PX_RIGHT, TEX_PX_BOTTOM,
		TEX_NX_LEFT, TEX_NX_TOP,
		TEX_NX_RIGHT, TEX_NX_TOP,
		TEX_NX_RIGHT, TEX_NX_BOTTOM,
		TEX_NX_LEFT, TEX_NX_BOTTOM
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
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_vertexBuffer = byteBuffer.asFloatBuffer();			// allocates the memory from the byte buffer
		_vertexBuffer.put(VERTICES);						// fill the vertexBuffer with the vertices
		_vertexBuffer.position(0);							// set the cursor position to the beginning of the buffer
		
		// Texture buffer
		byteBuffer = ByteBuffer.allocateDirect(TEX_COORDS.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_textureBuffer = byteBuffer.asFloatBuffer();
		_textureBuffer.put(TEX_COORDS);
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
