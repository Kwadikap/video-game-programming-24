import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * The TileMapRenderer class draws a TileMap on the screen.
 * It draws all tiles, sprites, and an optional background image
 * centered around the position of the player.
 * 
 * <p>
 * If the width of background image is smaller the width of
 * the tile map, the background image will appear to move
 * slowly, creating a parallax background effect.
 * 
 * <p>
 * Also, three static methods are provided to convert pixels
 * to tile positions, and vice-versa.
 * 
 * <p>
 * This TileMapRender uses a tile size of 64.
 */
public class TileMapRenderer {

    private static final int TILE_SIZE = 64;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;

    private BufferedImage background;

    /**
     * Converts a pixel position to a tile position.
     */
    public static int PixelsToTiles(float pixels) {
        return PixelsToTiles(Math.round(pixels));
    }

    /**
     * Converts a pixel position to a tile position.
     */
    public static int PixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        // return (int)Math.floor((float)pixels / TILE_SIZE);
    }

    /**
     * Converts a tile position to a pixel position.
     */
    public static int TilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        // return numTiles * TILE_SIZE;
    }

    /**
     * Sets the background to draw.
     */
    public void SetBackground(BufferedImage background) {
        float ratio = (Plat2D.SCREEN_HEIGHT / background.getHeight());
        int bgh = background.getHeight();
        int bgw = background.getWidth();

        int newW = (int) (bgw * ratio * 2);
        int newH = (int) (bgh * ratio * 2);

        Image tmp = background.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage newBg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newBg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        this.background = newBg;
    }

    /**
     * Draws the specified TileMap.
     */
    public void Draw(Graphics2D pen, TileMap map, int screenWidth, int screenHeight) {
        Player player = map.GetPlayer();
        int mapWidth = TilesToPixels(map.GetWidth());

        // get the scrolling position of the map
        // based on player's position
        int offsetX = screenWidth / 2 - Math.round(player.GetX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // get the y offset to draw all sprites and tiles
        int offsetY = screenHeight - TilesToPixels(map.GetHeight());

        // draw black background, if needed
        if (background == null || screenHeight > background.getHeight(null)) {
            // pen.setColor(Color.white);
            // pen.fillRect(0, 0, screenWidth, screenHeight);
        }

        // draw parallax background image
        if (background != null) {
            int x = offsetX *
                    (screenWidth - background.getWidth(null)) /
                    (screenWidth - mapWidth);
            int y = screenHeight - background.getHeight(null);

            pen.drawImage(background, x, y, null);
        }

        // draw the visible tiles
        int firstTileX = PixelsToTiles(-offsetX);
        int lastTileX = firstTileX + PixelsToTiles(screenWidth) + 1;

        for (int y = 0; y < map.GetHeight(); y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Rect rect = map.GetTile(x, y);

                if (rect != null) {
                    // Draw all tiles at an offset, this allows for scrolling
                    // rect.Draw(pen, offsetX, offsetY);

                    if (rect instanceof Wall) {
                        Wall wall = (Wall) rect;
                        wall.Draw(pen, offsetX, offsetY);
                    }

                    if (rect instanceof Creature) {
                        Creature creature = (Creature) rect;
                        creature.DrawSprite(pen, offsetX, offsetY);
                    }
                }

            }
        }

        // Draw player rectangle at an offset, allows for scrolling
        player.Draw(pen, offsetX, offsetY);

        // Draw player
        pen.drawImage(
                player.GetImage(),

                // This nightmare of an equation places the bottom-center of the image on the
                // bottom-center of the rectangle
                Math.round(player.GetX() + (player.GetW() / 2) - player.GetImage().getWidth(null) / 2) + offsetX,
                Math.round(player.GetY() + player.GetH() - player.GetImage().getHeight(null)) + offsetY,

                null);

        // player.GetImage().getWidth(null)/2) + offsetX,
        // Math.round((player.GetY() - player.GetImage().getHeight(null)) + offsetY),

        // Draw all other creatures
        Iterator<Rect> i = map.GetEntities();

        while (i.hasNext()) {
            Rect sprite = (Rect) i.next();

            int x = Math.round(sprite.GetX()) + offsetX;
            int y = Math.round(sprite.GetY()) + offsetY;

            sprite.Draw(pen, offsetX, offsetY);

            if (sprite instanceof Creature) {
                Creature creature = (Creature) sprite;

                // Draw creature
                pen.drawImage(
                        creature.GetImage(),

                        // This nightmare of an equation places the bottom-center of the image on the
                        // bottom-center of the rectangle
                        Math.round(creature.GetX() + (creature.GetW() / 2) - creature.GetImage().getWidth(null) / 2)
                                + offsetX,
                        Math.round(creature.GetY() + creature.GetH() - creature.GetImage().getHeight(null)) + offsetY,

                        null);

            }

            // wake up the creature when it's on screen
            if (sprite instanceof Creature && x >= 0 && x < screenWidth) {
                ((Creature) sprite).WakeUp();
            }
        }

    }

}
