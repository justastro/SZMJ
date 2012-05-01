package com.pigsar.szmj.graphic.old;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.pigsar.szmj.graphic.old.Renderable;
import com.pigsar.szmj.graphic.TextureManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * @author Justin Leung
 *
 */
public class Background implements Renderable {
	
	private static float s_vertices[] = {
		-2.0f, -1.0f,  0.0f,		// V1 - bottom left
		-2.0f,  1.0f,  0.0f,		// V2 - top left
		 2.0f, -1.0f,  0.0f,		// V3 - bottom right
		 2.0f,  1.0f,  0.0f			// V4 - top right
	};
	
	private static float s_texture[] = {    		
		// Mapping coordinates for the vertices
		0.0f, 0.5f,		// top left		(V2)
		0.0f, 0.0f,		// bottom left	(V1)
		1.0f, 0.5f,		// top right	(V4)
		1.0f, 0.0f		// bottom right	(V3)
	};
	
	private FloatBuffer _textureBuffer;	// buffer holding the texture coordinates
	private FloatBuffer _vertexBuffer;	// buffer holding the vertices
	private int[] _textures = new int[1];
	private int _resourceId;

	public Background(int drawableResourceId) {
		_resourceId = drawableResourceId;
		
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(s_vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		
		// allocates the memory from the byte buffer
		_vertexBuffer = byteBuffer.asFloatBuffer();
		
		// fill the vertexBuffer with the vertices
		_vertexBuffer.put(s_vertices);
		
		// set the cursor position to the beginning of the buffer
		_vertexBuffer.position(0);
		
		byteBuffer = ByteBuffer.allocateDirect(s_texture.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		_textureBuffer = byteBuffer.asFloatBuffer();
		_textureBuffer.put(s_texture);
		_textureBuffer.position(0);
	}
	
	/**
	 * Load the texture for the square
	 * @param gl
	 * @param context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		_textures[0] = TextureManager.instance().load(_resourceId);
	}

	
	/** The draw method for the square with the GL context */
	public void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -90.0f);
		
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
		
		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, s_vertices.length / 3);

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPopMatrix();
	}
}
