import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class Plat2D implements Runnable {
	// Intended Aspect Ratio is 4:3
	// Intended Resolution is 960 x 672

	protected ScreenManager sm;
	protected SoundManager soundManager;

	private static long initialTime = System.nanoTime();
	protected long timer = System.currentTimeMillis();

	private final int UPS = 60;
	private final int FPS = 60;

	private final double TIME_UPDATE = 1000000000 / UPS;
	private final double TIME_FRAMES = 1000000000 / FPS;

	private int frames = 0, ticks = 0;

	private float deltaUpdate = 0, deltaFrames = 0;

	public static int SCREEN_HEIGHT;

	// What starts the thread
	public void Init() {
		// Window
		sm = new ScreenManager();
		SCREEN_HEIGHT = sm.GetHeight();

		// Load Sound Manager
		soundManager = new SoundManager();

	}

	// What runs inside the thread
	public void run() {
		try {
			Init();
			GameLoop();
		} finally {
		}
	}

	// Defined Functions

	public void GameLoop() {
		soundManager.playBackgroundMusic();
		// Game Loop
		while (true) {

			long currentTime = System.nanoTime();

			deltaUpdate += (currentTime - initialTime) / TIME_UPDATE;
			deltaFrames += (currentTime - initialTime) / TIME_FRAMES;

			initialTime = currentTime;

			if (deltaUpdate >= 1) {
				GetInput();
				Update(deltaUpdate);
				ticks++; // for debugging purposes
				deltaUpdate--;
			}

			if (deltaFrames >= 1) {
				Render();
				frames++; // for debugging purposes
				deltaFrames--;
			}

			// For debugging purposes
			if (System.currentTimeMillis() - timer > 1000) {
				frames = 0;
				ticks = 0;
				timer += 1000;
			}

		}
	}

	// Update Screen
	private void Render() {
		// Drawing to the Screen
		Graphics2D pen = sm.GetGraphics();
		pen.clearRect(0, 0, sm.GetWidth(), sm.GetHeight());
		Draw(pen);
		pen.dispose();
		sm.Update();
	}

	// Updates state of game/animation based on amt of elapsed time that has passed
	public void Update(float deltaTime) {
	}

	// Draws to the screen. Subclasses must Override this method
	public abstract void Draw(Graphics2D pen);

	// Gets Input from the Player
	public abstract void GetInput();
}
