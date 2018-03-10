package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.GameLogic;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.DirectionalLight;
import org.lwjglb.engine.graph.Material;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.OBJLoader;
import org.lwjglb.engine.graph.PointLight;
import org.lwjglb.engine.graph.SpotLight;
import org.lwjglb.engine.graph.Texture;

public class TestGame implements GameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.05f;
    private final Vector3f cameraInc;

    private final Camera camera;
    private final Renderer renderer;
    private GameItem[] gameItems;
    private Vector3f ambientLight;
    private PointLight[] pointLightList;
    private SpotLight[] spotLightList;
    private DirectionalLight directionalLight;

    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;

    public TestGame() {
	renderer = new Renderer();
	camera = new Camera();
	cameraInc = new Vector3f(0, 0, 0);
	lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
	renderer.init(window);

	float reflectance = 1f;

	Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
	Texture texture = new Texture("/textures/grassblock.png");
	Material material = new Material(texture, reflectance);

	mesh.setMaterial(material);
	GameItem gameItem1 = new GameItem(mesh);
	gameItem1.setScale(0.5f);
	gameItem1.setPosition(0, 0, -2f);
	gameItems = new GameItem[] { gameItem1 };

	ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

	// Point Light
	Vector3f lightPosition = new Vector3f(0, 0, 1);
	float lightIntensity = 1.0f;
	PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
	PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
	pointLight.setAttenuation(att);
	pointLightList = new PointLight[]{pointLight};
	
	// Spot Light
	lightPosition = new Vector3f(0f, 0f, 10f);
	pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
	att = new PointLight.Attenuation(0.0f, 0.0f, 0.2f);
	pointLight.setAttenuation(att);
	Vector3f coneDir = new Vector3f(0, 0, -1);
	float cutoff = (float) Math.cos(Math.toRadians(140));
	SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
	spotLightList = new SpotLight[] {spotLight};

	// Directional Light
	lightPosition = new Vector3f(-1, 0, 0);
	directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
	cameraInc.set(0, 0, 0);
	if (window.isKeyPressed(GLFW_KEY_W)) {
	    cameraInc.z = -1;
	} else if (window.isKeyPressed(GLFW_KEY_S)) {
	    cameraInc.z = 1;
	}

	if (window.isKeyPressed(GLFW_KEY_A)) {
	    cameraInc.x = -1;
	} else if (window.isKeyPressed(GLFW_KEY_D)) {
	    cameraInc.x = 1;
	}

	if (window.isKeyPressed(GLFW_KEY_Z)) {
	    cameraInc.y = -1;
	} else if (window.isKeyPressed(GLFW_KEY_X)) {
	    cameraInc.y = 1;
	}

	float lightPos = spotLightList[0].getPointLight().getPosition().z;
	if (window.isKeyPressed(GLFW_KEY_N)) {
	    this.spotLightList[0].getPointLight().getPosition().z = lightPos + 0.1f;
	} else if (window.isKeyPressed(GLFW_KEY_M)) {
	    this.spotLightList[0].getPointLight().getPosition().z = lightPos - 0.1f;
	}
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
	camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
		cameraInc.z * CAMERA_POS_STEP);

	if (mouseInput.isRightButtonPressed()) {
	    Vector2f rotVec = mouseInput.getDisplVec();
	    camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
	}

	spotAngle += spotInc * .05f;
	if (spotAngle > 2) {
	    spotInc = -1;
	} else if (spotAngle < -2) {
	    spotInc = 1;
	}
	double spotAngleRad = Math.toRadians(spotAngle);
	Vector3f coneDir = spotLightList[0].getConeDirection();
	coneDir.y = (float) Math.sin(spotAngleRad);

	lightAngle += 1.1f;

	if (lightAngle > 90) {
	    directionalLight.setIntensity(0);
	    if (lightAngle >= 360) {
		lightAngle = -90;
	    }
	} else if (lightAngle <= -80 || lightAngle >= 80) {
	    float factor = 1 - (float) (Math.abs(lightAngle - 80)) / 10.0f;
	    directionalLight.setIntensity(factor);
	    directionalLight.getColour().y = Math.max(factor, 0.9f);
	    directionalLight.getColour().z = Math.max(factor, 0.5f);
	} else {
	    directionalLight.setIntensity(1f);
	    directionalLight.getColour().x = 1f;
	    directionalLight.getColour().y = 1f;
	    directionalLight.getColour().z = 1f;
	}

	double angRad = Math.toRadians(lightAngle);
	directionalLight.getDirection().x = (float) Math.sin(angRad);
	directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
	renderer.render(window, camera, gameItems, ambientLight, pointLightList, spotLightList, directionalLight);
    }

    @Override
    public void cleanup() {
	renderer.cleanup();
	for (GameItem gameItem : gameItems) {
	    gameItem.getMesh().cleanup();
	}
    }
}