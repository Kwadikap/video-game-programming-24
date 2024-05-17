
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Animation {
	private ArrayList<AnimFrame> frames;
	private int currFrameIndex;
	private float animTime;
	private float totalDuration;
	private boolean mirrored;

	// Creates a new, empty Animation
	public Animation() {
		this(new ArrayList(), 0);
	}

	private Animation(ArrayList<AnimFrame> frames, float totalDuration2) {
		this.frames = frames;
		this.totalDuration = totalDuration2;
		Start();
	}

	// Adds an Image to the Animation with the Specified Duration (time to display
	// image)
	public synchronized void AddFrame(BufferedImage img, float duration) {
		totalDuration += duration;
		frames.add(new AnimFrame(img, totalDuration));
	}

	// Starts the animation from the beginning
	public synchronized void Start() {
		animTime = 0;
		currFrameIndex = 0;
	}

	// Updates current image in animation if necessary
	public synchronized void Update(float deltaTime) {
		if (frames.size() > 1) {
			animTime += GameManager.delta;

			if (animTime >= totalDuration) {
				animTime = animTime % totalDuration;
				currFrameIndex = 0;
			}

			while (animTime > GetFrame(currFrameIndex).endTime) {
				currFrameIndex++;
			}
		}
	}

	// Gets animation's current image, returns null if animation has no images
	public synchronized BufferedImage GetImage() {
		if (frames.size() == 0) {
			return null;
		} else {
			return GetFrame(currFrameIndex).image;
		}
	}

	private AnimFrame GetFrame(int i) {
		return (AnimFrame) frames.get(i);
	}

	// Creates a duplicate of the animation.
	public Object Clone() {
		return new Animation(frames, totalDuration);
	}

	// Processes a sprite-sheet into an animation.
	// Processes an image (spritesheet) to make a full animation
	// Assumption File is in Assets folder
	public static Animation ReadSheet(String filename, int tileHeight, int tileWidth, float duration) {
		Animation anim = new Animation();
		BufferedImage spritesheet = null;

		try {
			spritesheet = ImageIO.read(new File("Assets/" + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int sheetWidth = spritesheet.getWidth(null) / tileWidth;
		int sheetHeight = spritesheet.getHeight(null) / tileHeight;
		BufferedImage sheet = (BufferedImage) spritesheet;

		for (int i = 0; i < sheetHeight; i++) {
			for (int j = 0; j < sheetWidth; j++) {
				anim.AddFrame((BufferedImage) sheet.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight),
						duration);
			}
		}

		return anim;
	}

	// Only copy a certain amount of images
	public static Animation ReadSheet(String filename, int tileHeight, int tileWidth, float duration, int amt) {
		Animation anim = new Animation();
		BufferedImage spritesheet = null;
		int iterator = 1;

		try {
			spritesheet = ImageIO.read(new File("Assets/" + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int sheetWidth = spritesheet.getWidth(null) / tileWidth;
		int sheetHeight = spritesheet.getHeight(null) / tileHeight;
		BufferedImage sheet = (BufferedImage) spritesheet;

		for (int i = 0; i < sheetHeight; i++) {
			for (int j = 0; j < sheetWidth; j++) {
				anim.AddFrame((BufferedImage) sheet.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight),
						duration);
				iterator++;
				if (iterator == amt) {
					break;
				}
			}
		}

		return anim;
	}

	// Flip every frame in an animation.
	public Animation FlipAnimation() {
		int index = 0;
		while (frames.size() > index) {
			frames.set(index, GetFlippedFrame((AnimFrame) frames.get(index)));
			index++;
		}

		return this;
	}

	// Mirror every frame in an animation.
	public Animation MirrorAnimation() {
		int index = 0;
		while (frames.size() > index) {
			frames.set(index, GetMirrorFrame((AnimFrame) frames.get(index)));
			index++;
		}

		// Toggle Flipped flag
		mirrored = !mirrored;

		return this;
	}

	// Scale every frame in an animation.
	public Animation ScaleAnimation(float dx, float dy) {
		int index = 0;
		while (frames.size() > index) {
			frames.set(index, GetScaledFrame((AnimFrame) frames.get(index), dx, dy));
			System.out.println("Index: " + index + " Mirrored");
			index++;
		}

		return this;
	}

	public Animation ScaleAnimation(float dx) {
		return ScaleAnimation(dx, dx);
	}

	// Mirrors a Frame Horizontally
	public AnimFrame GetMirrorFrame(AnimFrame frame) {
		frame.image = GetMirrorImage(frame.image);
		return frame;
	}

	// Flips a Frame Upside Down
	public AnimFrame GetFlippedFrame(AnimFrame frame) {
		frame.image = GetFlippedImage(frame.image);
		return frame;
	}

	// Scales a Frame
	public AnimFrame GetScaledFrame(AnimFrame frame, float dx, float dy) {
		frame.image = GetScaledImage(frame.image, dx, dy);
		return frame;
	}

	// Mirrors an Image
	public BufferedImage GetMirrorImage(BufferedImage image) {
		// Flip the image vertically
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}

	// Flips an Image Upside Down
	public BufferedImage GetFlippedImage(BufferedImage image) {
		// Flip the image vertically
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);

		return image;
	}

	// Expands an image by a percentage (1 = 100%)
	private BufferedImage GetScaledImage(BufferedImage image, float dx, float dy) {
		int w = image.getWidth();
		int h = image.getHeight();

		// Create new Image of proper size
		int w2 = Math.abs((int) (w * dx));
		int h2 = Math.abs((int) (h * dy));

		BufferedImage newImage = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);

		// set up the transform
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(dx, dy);
		AffineTransformOp scaleOperation = new AffineTransformOp(scaleInstance,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		// Transform the image onto the new image
		Graphics2D pen = (Graphics2D) newImage.getGraphics();
		pen.drawImage(image, scaleOperation, 0, 0);
		pen.dispose();

		return newImage;
	}

	// Get Animation Length
	public int GetLength() {
		return frames.size();
	}

	public int GetCurrFrame() {
		return currFrameIndex;
	}

	public boolean Ended() {
		return GetLength() - 1 == GetCurrFrame();
	}

	public boolean IsMirrored() {
		return mirrored;
	}

	private class AnimFrame {
		BufferedImage image;
		float endTime;

		public AnimFrame(BufferedImage image, float totalDuration) {
			this.image = image;
			this.endTime = totalDuration;
		}
	}

}
