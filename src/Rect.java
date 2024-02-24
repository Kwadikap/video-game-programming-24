import java.awt.*;

public class Rect {
    int x;
    int y;

    int w;
    int h;

    int old_x;
    int old_y;

    boolean held = false;

    public Rect(int x, int y, int w, int h) throws IllegalArgumentException {
        this.x = x;
        this.y = y;

        if (w < 0)
            throw new IllegalArgumentException("Cannot create a rectangle with a negative value for width");
        if (h < 0)
            throw new IllegalArgumentException("Caanot create a rectangle with a negative value for height");

        this.w = w;
        this.h = h;
    }

    public void draw(Graphics pen) {
        pen.drawRect(x, y, w, h);
    }

    public void grabbed() {
        held = true;
    }

    public void dropped() {
        held = false;
    }

    public void moveLeft(int dx) {
        old_x = x;

        x -= dx;
    }

    public void moveRight(int dx) {
        old_x = x;

        x += dx;
    }

    public void moveUp(int dy) {
        old_y = y;

        y -= dy;
    }

    public void jump(int dy) {
        for (int i = y; y <= y + dy; i++) {
            old_y = y;
            y -= i;
        }
    }

    public void moveDown(int dy) {
        old_y = y;

        y += dy;
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void resizeBy(int dw, int dh) {
        w += dw;

        h += dh;
    }

    public void chase(Rect r, int dx) {
        if (isLeftOf(r))
            moveRight(dx);
        if (isRightOf(r))
            moveLeft(dx);
        if (isAbove(r))
            moveDown(dx);
        if (isBelow(r))
            moveUp(dx);
    }

    public void evade(Rect r, int dx) {
        if (isLeftOf(r))
            moveLeft(dx);
        if (isRightOf(r))
            moveRight(dx);
        if (isAbove(r))
            moveUp(dx);
        if (isBelow(r))
            moveDown(dx);
    }

    public boolean isLeftOf(Rect r) {
        return x + w < r.x;
    }

    public boolean isRightOf(Rect r) {
        return r.x + r.w < x;
    }

    public boolean isAbove(Rect r) {
        return y + h < r.y;
    }

    public boolean isBelow(Rect r) {
        return r.y + r.h < y;
    }

    public boolean contains(int mx, int my) {
        return (mx >= x) &&

                (mx <= x + w) &&

                (my >= y) &&

                (my <= y + h);
    }

    public boolean overlaps(Rect r) {
        return (x + w >= r.x) &&

                (r.x + r.w >= x) &&

                (y + h >= r.y) &&

                (r.y + r.h >= y);
    }

    public void pushOutOf(Rect r) {
        if (cameFromAbove(r))
            pushbackUpFrom(r);

        if (cameFromBelow(r))
            pushbackDownFrom(r);

        if (cameFromLeft(r))
            pushbackLeftFrom(r);

        if (cameFromRight(r))
            pushbackRightFrom(r);
    }

    public boolean cameFromLeft(Rect r) {
        return old_x + w < r.x;
    }

    public boolean cameFromRight(Rect r) {
        return r.x + r.w < old_x;
    }

    public boolean cameFromAbove(Rect r) {
        return old_y + h < r.y;
    }

    public boolean cameFromBelow(Rect r) {
        return r.y + r.h < old_y;
    }

    public void pushbackLeftFrom(Rect r) {
        x = r.x - w - 1;
    }

    public void pushbackRightFrom(Rect r) {
        x = r.x + r.w + 1;
    }

    public void pushbackUpFrom(Rect r) {
        y = r.y - h - 1;
    }

    public void pushbackDownFrom(Rect r) {
        y = r.y + r.h + 1;
    }

    public String toString() {
        return "new Rect(" + x + ", " + y + ", " + w + ", " + h + "),";
    }
}
