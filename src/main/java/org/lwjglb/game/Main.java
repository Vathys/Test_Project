package org.lwjglb.game;

import org.lwjglb.engine.GameEngine;
import org.lwjglb.engine.GameLogic;

public class Main
{
	public static void main(String[] args)
	{
		try {
			boolean vSync = true;
			GameLogic gameLogic = new TestGame();
			GameEngine gameEngine = new GameEngine("Test Game", 640 * 2, 480 * 2, vSync, gameLogic);
			gameEngine.start();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
