import java.awt.Graphics;

public class Sprite extends Rect {
    String[] movement = {
            "down",
            "up",
            "left",
            "right",
    };

    Animation[] animation;

    int action = 0;

    boolean isMoving = false;

    public Sprite(String name, int x, int y, int count, int duration) {
        super(x, y, 50, 100);

        animation = new Animation[movement.length];

        // initialize animation array with movement animations
        for (int i = 0; i < animation.length; i++) {
            animation[i] = new Animation(name + "_" + movement[i], count, duration);
        }
    }

    public void moveLeft(int dx) {
        old_x = x;

        action = 2;

        x -= dx;

        isMoving = true;
    }

    public void moveRight(int dx) {
        old_x = x;

        action = 3;

        x += dx;

        isMoving = true;
    }

    public void moveUp(int dy) {
        old_y = y;

        action = 1;

        y -= dy;

        isMoving = true;
    }

    public void moveDown(int dy) {
        old_y = y;

        action = 0;

        y += dy;

        isMoving = true;
    }

    public void draw(Graphics pen) {
        if (!isMoving) {
            pen.drawImage(animation[action].stillImage(), x, y, w, h, null);
        } else {
            pen.drawImage(animation[action].nextImage(), x, y, w, h, null);
        }
    }
}
