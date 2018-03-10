package org.lwjglb.engine;

public class GameEngine implements Runnable {
    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final GameLogic gameLogic;
    private final MouseInput mouseInput;

    public GameEngine(String title, int width, int height, boolean vSync, GameLogic gameLogic) throws Exception {
	gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
	window = new Window(title, width, height, vSync);
	mouseInput = new MouseInput();
	this.gameLogic = gameLogic;
	timer = new Timer();
    }

    public void start() {
	String osName = System.getProperty("os.name");
	if (osName.contains("Mac")) {
	    gameLoopThread.run();
	} else {
	    gameLoopThread.start();
	}
    }

    public void run() {
	try {
	    init();
	    gameLoop();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cleanup();
	}
    }

    protected void init() throws Exception {
	window.init();
	timer.init();
	mouseInput.init(window);
	gameLogic.init(window);
    }

    protected void gameLoop() {
	float elapsedTime;
	float accumulator = 0f;
	float interval = 1f / TARGET_UPS;

	boolean running = true;
	while (running && !window.windowShouldClose()) {
	    elapsedTime = timer.getElapsedTime();
	    accumulator += elapsedTime;

	    input();

	    while (accumulator >= interval) {
		update(interval);
		accumulator -= interval;
	    }

	    render();

	    if (!window.isVSync()) {
		sync();
	    }
	}
    }

    protected void cleanup() {
	gameLogic.cleanup();
    }

    private void sync() {
	float loopSlot = 1f / TARGET_FPS;
	double endTime = timer.getLastLoopTime() + loopSlot;
	while (timer.getTime() < endTime) {
	    try {
		Thread.sleep(1);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    protected void input() {
	mouseInput.input(window);
	gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
	gameLogic.update(interval, mouseInput);
    }

    protected void render() {
	gameLogic.render(window);
	window.update();
    }
}
