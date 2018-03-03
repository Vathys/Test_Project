package org.lwjglb.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.Utils;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.ShaderProgram;
import org.lwjglb.engine.graph.Transformation;

public class Renderer {

	private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float Z_NEAR = 0.01f;
	private static final float Z_FAR = 1000.0f;
	
	private final Transformation transformation;
    private ShaderProgram shaderProgram;

    public Renderer() {
    	transformation = new Transformation();
    }
    
    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        
        shaderProgram.createVertexShader(Utils.loadResources("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResources("/fragment.fs"));
        shaderProgram.link();
        
        shaderProgram.createUniforms("projectionMatrix");
        shaderProgram.createUniforms("worldMatrix");
        
        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);
        
        for(GameItem gameItem : gameItems) {
        	Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem.getPosition(), gameItem.getRotation(), gameItem.getScale());
        	shaderProgram.setUniform("worldMatrix", worldMatrix);
        	gameItem.getMesh().render();
        }
        
        shaderProgram.unbind();
    }

    public void cleanup() {
        if(shaderProgram != null) {
        	shaderProgram.cleanup();
        }
    }
}