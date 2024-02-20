
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.event.MouseEvent;

public class GameEngine extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    Image background = Toolkit.getDefaultToolkit().getImage("BG.png");
    Image land1 = Toolkit.getDefaultToolkit().getImage("1.png");
    Image land2 = Toolkit.getDefaultToolkit().getImage("2.png");
    Image land3 = Toolkit.getDefaultToolkit().getImage("3.png");
    Image water = Toolkit.getDefaultToolkit().getImage("17.png");
    Image water2 = Toolkit.getDefaultToolkit().getImage("18.png");
    int mouseX, mouseY;

    int moveSpeed = 10;

    boolean up_pressed, down_pressed, left_pressed, right_pressed = false;

    Rect player1 = new Rect(600, 100, 30, 30);

    ResizableRect[] walls = {
            new ResizableRect(200, 200, 100, 100),
            new ResizableRect(200, 400, 100, 100),
            new ResizableRect(200, 600, 100, 100),
    };

    public void init() {
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
                player1.moveUp(moveSpeed);
            if (down_pressed)
                player1.moveDown(moveSpeed);
            if (left_pressed)
                player1.moveLeft(moveSpeed);
            if (right_pressed)
                player1.moveRight(moveSpeed);

            for (ResizableRect rect : walls) {
                if (player1.overlaps(rect))
                    player1.pushOutOf(rect);
            }

            repaint();

            try {
                Thread.sleep(16);
            } catch (Exception ex) {

            }

        }
    }

    public void update(Graphics pen) {
        pen.clearRect(0, 0, 2560, 1600);

        paint(pen);
    }

    public void paint(Graphics pen) {
        pen.setColor(Color.BLACK);

        pen.drawImage(background, 0, 0, 1500, 900, null);
        pen.drawImage(water2, 370, 700, 450, 175, null);
        pen.drawImage(water, 390, 630, 100, 75, null);
        pen.drawImage(water, 490, 630, 100, 75, null);
        pen.drawImage(water, 590, 630, 100, 75, null);

        pen.drawImage(land2, 0, 600, 200, 200, null);
        pen.drawImage(land3, 200, 600, 200, 200, null);
        pen.drawImage(land1, 600, 600, 200, 200, null);
        pen.drawImage(land2, 800, 600, 200, 200, null);
        pen.drawImage(land2, 1000, 600, 200, 200, null);

        player1.draw(pen);

        for (ResizableRect rect : walls) {
            rect.draw(pen);
        }

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
/*
 * <applet code="GameEngine" width=1500 height=920>
 * </applet>
 */