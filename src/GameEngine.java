
/*
 * <applet code="GameEngine" width=1500 height=920>
 * </applet>
 */

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.event.MouseEvent;

public class GameEngine extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    int mouseX = -1;
    int mouseY = -1;

    int moveSpeed = 10;

    boolean up_pressed = false;
    boolean down_pressed = false;
    boolean left_pressed = false;
    boolean right_pressed = false;

    Image offScreenImg;
    Graphics offScreenPen;

    Sprite player = new Sprite("player", 100, 100, 5, 15);

    public void init() {
        offScreenImg = createImage(1920, 1200);
        offScreenPen = offScreenImg.getGraphics();

        addKeyListener(this);
        requestFocus();

        addMouseListener(this);

        addMouseMotionListener(this);

        Thread thread = new Thread(this);
        // Thread where the game loop will run
        thread.start();
    }

    public void run() {
        // Game loop
        while (true) {
            if (up_pressed)
                player.moveUp(moveSpeed);
            if (down_pressed)
                player.moveDown(moveSpeed);
            if (left_pressed)
                player.moveLeft(moveSpeed);
            if (right_pressed)
                player.moveRight(moveSpeed);

            repaint();

            try {
                Thread.sleep(16);
            } catch (Exception ex) {

            }

        }
    }

    public void update(Graphics pen) {
        offScreenPen.clearRect(0, 0, 1920, 1200);

        paint(offScreenPen);

        pen.drawImage(offScreenImg, 0, 0, null);

    }

    public void paint(Graphics pen) {
        // draw player
        player.draw(pen);
    }

    @SuppressWarnings("static-access")
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == e.VK_UP)
            up_pressed = true;
        if (code == e.VK_DOWN)
            down_pressed = true;
        if (code == e.VK_LEFT)
            left_pressed = true;
        if (code == e.VK_RIGHT)
            right_pressed = true;

    }

    @SuppressWarnings("static-access")
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == e.VK_UP)
            up_pressed = false;
        if (code == e.VK_DOWN)
            down_pressed = false;
        if (code == e.VK_LEFT)
            left_pressed = false;
        if (code == e.VK_RIGHT)
            right_pressed = false;
    }

    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        for (ResizableRect rect : walls) {
            if (rect.contains(mouseX, mouseY))
                rect.grabbed();
            if (rect.resizer.contains(mouseX, mouseY))
                rect.resizer.grabbed();
        }
    }

    public void mouseReleased(MouseEvent e) {
        for (ResizableRect rect : walls) {
            rect.dropped();
            rect.resizer.dropped();
        }
    }

    public void mouseDragged(MouseEvent e) {
        int newMouseX = e.getX();
        int newMouseY = e.getY();

        int dx = newMouseX - mouseX;
        int dy = newMouseY - mouseY;

        for (ResizableRect rect : walls) {
            if (rect.resizer.held)
                rect.resizeBy(dx, dy);
            else if (rect.held)
                rect.moveBy(dx, dy);
        }

        mouseX = newMouseX;
        mouseY = newMouseY;

    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {

    }
}
