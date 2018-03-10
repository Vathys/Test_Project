package org.lwjglb.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.system.MemoryUtil;

public class Mesh {
    private final int vaoID;
    private final List<Integer> vboIdList;
    private final int vertexCount;
    private Material material;

    public Mesh(float[] positions, float[] textCoord, float[] normals, int[] indices) {
	FloatBuffer posBuffer = null;
	FloatBuffer textureBuffer = null;
	FloatBuffer vecNormalBuffer = null;
	IntBuffer indicesBuffer = null;
	try {
	    vertexCount = indices.length;
	    vboIdList = new ArrayList<>();

	    vaoID = glGenVertexArrays();
	    glBindVertexArray(vaoID);

	    // Position VBO
	    int vboId = glGenBuffers();
	    vboIdList.add(vboId);
	    posBuffer = MemoryUtil.memAllocFloat(positions.length);
	    posBuffer.put(positions).flip();
	    glBindBuffer(GL_ARRAY_BUFFER, vboId);
	    glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

	    // Texture VBO
	    vboId = glGenBuffers();
	    vboIdList.add(vboId);
	    textureBuffer = MemoryUtil.memAllocFloat(textCoord.length);
	    textureBuffer.put(textCoord).flip();
	    glBindBuffer(GL_ARRAY_BUFFER, vboId);
	    glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
	    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

	    // Vertex Normals VBO
	    vboId = glGenBuffers();
	    vboIdList.add(vboId);
	    vecNormalBuffer = MemoryUtil.memAllocFloat(normals.length);
	    vecNormalBuffer.put(normals).flip();
	    glBindBuffer(GL_ARRAY_BUFFER, vboId);
	    glBufferData(GL_ARRAY_BUFFER, vecNormalBuffer, GL_STATIC_DRAW);
	    glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

	    // Index VBO
	    vboId = glGenBuffers();
	    vboIdList.add(vboId);
	    indicesBuffer = MemoryUtil.memAllocInt(indices.length);
	    indicesBuffer.put(indices).flip();
	    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
	    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

	    glBindBuffer(GL_ARRAY_BUFFER, 0);
	    glBindVertexArray(0);
	} finally {
	    if (posBuffer != null) {
		MemoryUtil.memFree(posBuffer);
	    }
	    if (textureBuffer != null) {
		MemoryUtil.memFree(textureBuffer);
	    }
	    if (vecNormalBuffer != null) {
		MemoryUtil.memFree(vecNormalBuffer);
	    }
	    if (indicesBuffer != null) {
		MemoryUtil.memFree(indicesBuffer);
	    }
	}
    }

    public Material getMaterial() {
	return material;
    }

    public void setMaterial(Material material) {
	this.material = material;
    }

    public int getVaoID() {
	return vaoID;
    }

    public int getVertexCount() {
	return vertexCount;
    }

    public void render() {
	Texture texture = material.getTexture();
	if (texture != null) {
	    glActiveTexture(GL_TEXTURE0);
	    glBindTexture(GL_TEXTURE_2D, texture.getId());
	}

	glBindVertexArray(getVaoID());
	glEnableVertexAttribArray(0);
	glEnableVertexAttribArray(1);
	glEnableVertexAttribArray(2);

	glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

	glDisableVertexAttribArray(0);
	glDisableVertexAttribArray(1);
	glDisableVertexAttribArray(2);
	glBindVertexArray(0);
	glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
	glDisableVertexAttribArray(0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	for (int vboId : vboIdList) {
	    glDeleteBuffers(vboId);
	}

	Texture texture = material.getTexture();
	if (texture != null) {
	    texture.cleanup();
	}

	glBindVertexArray(0);
	glDeleteVertexArrays(vaoID);

    }
}