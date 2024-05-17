import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Wall extends Rect {

	public Wall(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public void AssignImage(BufferedImage img) {
		anim = new Animation();
		anim.AddFrame(img, 100);

	}

	// Collision Overrides
	public void OnCollisionRT(Rect neighbor) {
		neighbor.PushbackFrmRT(this);
	}

	public void OnCollisionLT(Rect neighbor) {
		neighbor.PushbackFrmLT(this);
	}

	public void OnCollisionUP(Rect neighbor) {
		neighbor.PushbackFrmUP(this);
	}

	public void OnCollisionDN(Rect neighbor) {
		neighbor.PushbackFrmDN(this);
	}

	public void Draw(Graphics2D pen, float dx, float dy) {
		if (anim != null) {
			pen.drawImage(anim.GetImage(), (int) (x + dx), (int) (y + dy), width, height, null);
		}
	}

}
