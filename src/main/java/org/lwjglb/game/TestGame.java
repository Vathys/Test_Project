package org.lwjglb.game;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.GameLogic;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Mesh;


public class TestGame implements GameLogic
{
	private int displxInc = 0;
	private int displyInc = 0;
	private int displzInc = 0;
	private int scaleInc = 0;
	
	private final Renderer renderer;
	private GameItem[] gameItems;
	private int temp = 0;
	
	public TestGame() {
		renderer = new Renderer();
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
		
		float[] positions = new float[]{
			-0.5f, 0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f 
		};
		
		float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f,
			0.0f, 0.0f, 0.5f,
			0.0f, 0.5f, 0.5f
		};
		int[] indices = new int[] {
			0, 1, 3, 3, 1, 2
		};
		Mesh mesh = new Mesh(positions, colours, indices);
		GameItem gameItem = new GameItem(mesh);
		gameItem.setPosition(0, 0, -1000); //working start coordinates (0, 0, -1000)
		gameItems = new GameItem[]{gameItem};
	}

	@Override
	public void input(Window window) {
		displxInc = 0;
		displyInc = 0;
		displzInc = 0;
		scaleInc = 0;
		if(window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			displyInc = 1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			displyInc = -1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			displxInc = -1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			displxInc = 1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_A)) {
			displzInc = -1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
			displzInc = 1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			scaleInc = -1;
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_X)) {
			scaleInc = 1;
		}
	}

	@Override
	public void update(float interval) {
		for(GameItem gameItem : gameItems) {
			Vector3f position = gameItem.getPosition();
			float posx = position.x + displxInc * 0.0001f;
			float posy = position.y + displyInc * 0.0001f;
			float posz = position.z + displzInc * 0.0001f;
			gameItem.setPosition(posx, posy, posz);
			
			float scale = gameItem.getScale();
			scale += scaleInc * 0.0001f;
			if(scale < 0) {
				scale = 0;
			}
			gameItem.setScale(scale);
			if(temp == 1000) {
				//System.out.println("(" + posx + ", " + posy + ", " + posz + ")");
				temp = 0;
			}
			
			float rotation = gameItem.getRotation().z + 1.5f;
			if(rotation > 360) {
				rotation = 0;
			}
			gameItem.setRotation(0, 0, rotation);
			temp++;
		}
	}

	@Override
	public void render(Window window) {
		renderer.render(window, gameItems);
	}

	@Override
	public void cleanup()
	{
		renderer.cleanup();
		for(GameItem gameItem : gameItems) {
			gameItem.getMesh().cleanup();
		}
	}
}
