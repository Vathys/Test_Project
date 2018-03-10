package org.lwjglb.engine.graph;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    private final int id;

    public Texture(String filename) throws Exception {
	this(loadTexture(filename));
    }

    public Texture(int id) {
	this.id = id;
    }

    public void bind() {
	glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
	return id;
    }

    private static int loadTexture(String filename) throws Exception {
	PNGDecoder decoder = new PNGDecoder(Texture.class.getResourceAsStream(filename));

	ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
	decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
	buf.flip();

	int textureID = glGenTextures();
	glBindTexture(GL_TEXTURE_2D, textureID);

	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

	// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
		buf);
	glGenerateMipmap(GL_TEXTURE_2D);
	return textureID;
    }

    public void cleanup() {
	glDeleteTextures(id);
    }
}
