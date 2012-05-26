package com.pigsar.szmj.graphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.Tile;

public class TileFaceRenderer {
	
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
	
	private TileRenderer _tileRenderer;

	private FloatBuffer _textureBuffer;	// buffer holding the texture coordinates
	private FloatBuffer _vertexBuffer;	// buffer holding the vertices
	private int[] _textures = new int[1];
	
	public TileFaceRenderer(TileRenderer tileRenderer) {
		_tileRenderer = tileRenderer;
	}
	
	public void initializeResource() {
		_textures[0] = TextureManager.instance().load(R.drawable.mahjong_a);
		
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(s_vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_vertexBuffer = byteBuffer.asFloatBuffer();			// allocates the memory from the byte buffer
		_vertexBuffer.put(s_vertices);						// fill the vertexBuffer with the vertices
		_vertexBuffer.position(0);							// set the cursor position to the beginning of the buffer
		
		float[] textures = new float[42 * 8];
		for (int i = 0; i < 42; ++i) {
			float paddingX = (i % 8) * (1.0f / 8.0f);
			float paddingY = (i / 8) * (1.0f / 8.0f);
			int b = i * 8;
			
			textures[b+0] = paddingX + kTileFaceTexRight;
			textures[b+1] = paddingY + kTileFaceTexBottom;
			textures[b+2] = paddingX + kTileFaceTexRight;
			textures[b+3] = paddingY + kTileFaceTexTop;
			textures[b+4] = paddingX + kTileFaceTexLeft;
			textures[b+5] = paddingY + kTileFaceTexTop;
			textures[b+6] = paddingX + kTileFaceTexLeft;
			textures[b+7] = paddingY + kTileFaceTexBottom;
		}
		
		byteBuffer = ByteBuffer.allocateDirect(textures.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_textureBuffer = byteBuffer.asFloatBuffer();
		_textureBuffer.put(textures);
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
			
			_textureBuffer.position(tile.pattern().textureIndex() * 8);
		
			// Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer);
			
			// Draw the vertices as triangle fan
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
			
			gl.glPopMatrix();
		}

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
