package org.lwjglb.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

public class Mesh
{
	private final int vaoID;
	private final int posVboID;
	private final int colourVboID;
	private final int idxVboID;
	private final int vertexCount;
	
	public Mesh(float[] positions, float[] colours, int[] indices) {
		FloatBuffer posBuffer = null;
		FloatBuffer colourBuffer = null;
		IntBuffer indicesBuffer = null;
		try {
			vertexCount = indices.length;
			
			vaoID = glGenVertexArrays();
			glBindVertexArray(vaoID);
			
			posVboID = glGenBuffers();
			posBuffer = MemoryUtil.memAllocFloat(positions.length);
			posBuffer.put(positions).flip();
			glBindBuffer(GL_ARRAY_BUFFER, posVboID);
			glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			
			colourVboID = glGenBuffers();
			colourBuffer = MemoryUtil.memAllocFloat(colours.length);
			colourBuffer.put(colours).flip();
			glBindBuffer(GL_ARRAY_BUFFER, colourVboID);
			glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
			
			idxVboID = glGenBuffers();
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboID);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			if(posBuffer != null) {
				MemoryUtil.memFree(posBuffer);
			}
			if(colourBuffer != null) {
				MemoryUtil.memFree(colourBuffer);
			}
			if(indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public void render() {
		glBindVertexArray(getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}
	
	public void cleanup() {
		glDisableVertexAttribArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(posVboID);
		glDeleteBuffers(colourVboID);
		glDeleteBuffers(idxVboID);
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);
		
	}
}
