import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;

public class ScreenManager {
	private GraphicsDevice device;
	JFrame window;
	int windowWidth, windowHeight;

	public ScreenManager() {
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		windowWidth = device.getDisplayMode().getWidth();
		windowHeight = device.getDisplayMode().getHeight();
		CreateWindow("Plat2D");
	}

	public void CreateWindow(String windowTitle) {

		window = new JFrame(windowTitle);

		window.setFocusable(true);
		window.requestFocusInWindow();

		window.setSize(windowWidth, windowHeight);
		window.setVisible(true);
		window.setFocusable(true);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// avoid potential deadlock in 1.4.1_02
		try {
			EventQueue.invokeAndWait(
					new Runnable() {
						public void run() {
							window.createBufferStrategy(2);
						}
					});
		} catch (InterruptedException ex) {
		} catch (InvocationTargetException ex) {
		}
	}

	public Graphics2D GetGraphics() {
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			return (Graphics2D) strategy.getDrawGraphics();
		}

		else {
			return null;
		}
	}

	public void Update() {
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			if (!strategy.contentsLost()) {
				strategy.show();
			}
		}
	}

	public int GetWidth() {
		return window.getWidth();
	}

	public int GetHeight() {
		return window.getHeight();
	}

	public Container GetWindow() {
		return window;
	}

	public void SetContentPane(Container contentPane) {
		window.setContentPane(contentPane);
	}

}
