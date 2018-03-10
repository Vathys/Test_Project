package org.lwjglb.game;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.Utils;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.DirectionalLight;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.PointLight;
import org.lwjglb.engine.graph.ShaderProgram;
import org.lwjglb.engine.graph.SpotLight;
import org.lwjglb.engine.graph.Transformation;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private float specularPower;

    private final Transformation transformation;
    private ShaderProgram shaderProgram;

    public Renderer() {
	transformation = new Transformation();
	specularPower = 10f;
    }

    public void init(Window window) throws Exception {
	shaderProgram = new ShaderProgram();

	shaderProgram.createVertexShader(Utils.loadResources("/shaders/vertex.vs"));
	shaderProgram.createFragmentShader(Utils.loadResources("/shaders/fragment.fs"));
	shaderProgram.link();

	shaderProgram.createUniforms("projectionMatrix");
	shaderProgram.createUniforms("modelViewMatrix");
	shaderProgram.createUniforms("texture_sampler");
	shaderProgram.createUniforms("specularPower");
	shaderProgram.createUniforms("ambientLight");
	shaderProgram.createMaterialUniform("material");
	shaderProgram.createPointLightListUniform("pointLight", MAX_POINT_LIGHTS);
	shaderProgram.createSpotLightListUniform("spotLight", MAX_SPOT_LIGHTS);
	shaderProgram.createDirectionalLight("directionalLight");
    }

    public void clear() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight,
	    PointLight[] pointLight, SpotLight[] spotLight, DirectionalLight directionalLight) {
	clear();

	if (window.isResized()) {
	    glViewport(0, 0, window.getWidth(), window.getHeight());
	    window.setResized(false);
	}

	shaderProgram.bind();

	Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
		Z_NEAR, Z_FAR);
	shaderProgram.setUniform("projectionMatrix", projectionMatrix);

	Matrix4f viewMatrix = transformation.getViewMatrix(camera);

	renderLights(viewMatrix, ambientLight, pointLight, spotLight, directionalLight);
	
	shaderProgram.setUniform("texture_sampler", 0);

	for (GameItem gameItem : gameItems) {
	    Mesh mesh = gameItem.getMesh();
	    Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
	    shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

	    shaderProgram.setUniform("material", mesh.getMaterial());
	    mesh.render();
	}

	shaderProgram.unbind();
    }

    public void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, PointLight[] pointLight, SpotLight[] spotLight,
	    DirectionalLight directionalLight) {
	shaderProgram.setUniform("ambientLight", ambientLight);
	shaderProgram.setUniform("specularPower", specularPower);

	int numLights = pointLight != null ? pointLight.length : 0;
	for (int i = 0; i < numLights; i++) {

	    PointLight currPointLight = new PointLight(pointLight[i]);
	    Vector3f lightPos = currPointLight.getPosition();
	    Vector4f aux = new Vector4f(lightPos, 1);
	    aux.mul(viewMatrix);
	    lightPos.x = aux.x;
	    lightPos.y = aux.y;
	    lightPos.z = aux.z;
	    shaderProgram.setUniform("pointLight", currPointLight, i);

	}

	numLights = spotLight != null ? spotLight.length : 0;
	for (int i = 0; i < numLights; i++) {
	    SpotLight currSpotLight = new SpotLight(spotLight[i]);
	    Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
	    dir.mul(viewMatrix);
	    currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

	    Vector3f spotLightPos = currSpotLight.getPointLight().getPosition();
	    Vector4f auxSpot = new Vector4f(spotLightPos, 1);
	    auxSpot.mul(viewMatrix);
	    spotLightPos.x = auxSpot.x;
	    spotLightPos.y = auxSpot.y;
	    spotLightPos.z = auxSpot.z;

	    shaderProgram.setUniform("spotLight", currSpotLight, i);
	}
	DirectionalLight currDirLight = new DirectionalLight(directionalLight);
	Vector4f dir = new Vector4f(currDirLight.getDirection(), 0.0f);
	dir.mul(viewMatrix);
	currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
	shaderProgram.setUniform("directionalLight", currDirLight);
    }

    public void cleanup() {
	if (shaderProgram != null) {
	    shaderProgram.cleanup();
	}
    }
}