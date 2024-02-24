import java.awt.*;

public class Animation {
    private Image[] image;
    private int next;

    private int duration;
    private int delay;

    public Animation(String name, int count, int duration) {
        image = new Image[count];

        // Load all images into images array
        for (int i = 0; i < count; i++) {
            image[i] = Toolkit.getDefaultToolkit().getImage(name + "_" + i + ".png");
        }

        this.duration = duration;
        delay = duration;
    }

    // Image where character is not moving
    public Image stillImage() {
        return image[0];
    }

    public Image nextImage() {
        if (delay == 0) {
            next++;
            // loop back to beginning of animation once at last frame
            if (next == image.length)
                next = 1;

            delay = duration;
        }

        delay--;

        return image[next];
    }

}
