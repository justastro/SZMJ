package com.pigsar.szmj.graphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.R;
import com.pigsar.szmj.library.Tile;

public class TileFaceRenderer {
	
	private static float VERTICES[] = {
		TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2,
		-TileRenderer.TILE_WIDTH_2, -TileRenderer.TILE_HEIGHT_2, TileRenderer.TILE_LENGTH_2
	};
	
	private static final float TEX_SIZE				= 1024.0f;
	private static final float TEX_LEFT				= 18.0f;
	private static final float TEX_TOP				= 2.0f;
	private static final float TILE_TEX_WIDTH		= 92.0f;
	private static final float TILE_TEX_HEIGHT		= 124.0f;
	private static final float TEX_LEFT_UV			= (TEX_LEFT / TEX_SIZE);
	private static final float TEX_RIGHT_UV			= ((TEX_LEFT + TILE_TEX_WIDTH) / TEX_SIZE);
	private static final float TEX_TOP_UV			= (TEX_TOP / TEX_SIZE);
	private static final float TEX_BOTTOM_UV		= ((TEX_TOP + TILE_TEX_HEIGHT) / TEX_SIZE);
	
//	private static float TEX_COORDS[] = {
//		TEX_RIGHT_RATIO, TEX_BOTTOM_RATIO,
//		TEX_RIGHT_RATIO, TEX_TOP_RATIO,
//		TEX_LEFT_RATIO, TEX_TOP_RATIO,
//		TEX_LEFT_RATIO, TEX_BOTTOM_RATIO
//	};
	
	private TileRenderer _tileRenderer;

	private FloatBuffer _textureBuffer;			// buffer holding the texture coordinates
	private FloatBuffer _vertexBuffer;			// buffer holding the vertices
	private int[] _textures = new int[1];
	
	public TileFaceRenderer(TileRenderer tileRenderer) {
		_tileRenderer = tileRenderer;
	}
	
	public void initializeResource() {
		_textures[0] = TextureManager.instance().load(R.drawable.mahjong_a);
		
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_vertexBuffer = byteBuffer.asFloatBuffer();			// allocates the memory from the byte buffer
		_vertexBuffer.put(VERTICES);						// fill the vertexBuffer with the vertices
		_vertexBuffer.position(0);							// set the cursor position to the beginning of the buffer
		
		float[] textures = new float[42 * 8];
		for (int i = 0; i < 42; ++i) {
			float paddingX = (i % 8) * (1.0f / 8.0f);
			float paddingY = (i / 8) * (1.0f / 8.0f);
			int b = i * 8;
			
			textures[b+0] = paddingX + TEX_RIGHT_UV;
			textures[b+1] = paddingY + TEX_BOTTOM_UV;
			textures[b+2] = paddingX + TEX_RIGHT_UV;
			textures[b+3] = paddingY + TEX_TOP_UV;
			textures[b+4] = paddingX + TEX_LEFT_UV;
			textures[b+5] = paddingY + TEX_TOP_UV;
			textures[b+6] = paddingX + TEX_LEFT_UV;
			textures[b+7] = paddingY + TEX_BOTTOM_UV;
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
