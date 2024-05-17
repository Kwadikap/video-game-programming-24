
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/* RESOURCEMANAGER.JAVA
 * Loads Images, Creates animations and Sprites, and Loads levels
 */

public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;

    // Host sprites used for cloning
    private Player player = new Player(64, 64, 32, 64);
    private Beholder beholder = new Beholder(64, 64, 64, 64);
    private EvilWizard ewiz = new EvilWizard(64, 64, 64, 100);

    // TileLists
    private BufferedImage[] a = ReadSheet("OakWoods/OakWoods_A", 24, 24);

    public ResourceManager() {
        LoadCreatureSprites();
    }

    public TileMap LoadNextMap() {

        TileMap map = null;

        while (map == null) {
            currentMap++;
            try {
                map = LoadMap("Maps/map" + currentMap + ".txt");
            } catch (IOException ex) {
                // no maps to load!
                if (currentMap == 1) {
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }

    public TileMap ReloadMap() {
        try {
            player.respawn();
            return LoadMap("Maps/map" + currentMap + ".txt");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Loading Tile-Map Text Files
    private TileMap LoadMap(String filename) throws IOException {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // Read every line in the text file into the list
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        while (true) {
            String line = reader.readLine();

            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();

        TileMap newMap = new TileMap(width, height);

        for (int y = 0; y < height; y++) {
            String line = (String) lines.get(y);

            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';

                if (tile >= 0) {
                    int tileNo = LookAround(lines, ch, x, y, width, height);

                    Wall newtile = new Wall(TileMapRenderer.TilesToPixels(x), TileMapRenderer.TilesToPixels(y), 64, 64);

                    newtile.AssignImage(a[tileNo]);

                    newMap.SetTile(x, y, newtile);
                }

                // check if the char represents a sprite
                else if (ch == '!') {
                    AddRect(newMap, beholder, x, y);
                } else if (ch == 'o') {
                    /* addSprite(newMap, musicSprite, x, y); */ } else if (ch == '*') {
                    AddRect(newMap, ewiz, x, y);
                } else if (ch == '1') {
                    /* addSprite(newMap, grubSprite, x, y); */ } else if (ch == '2') {
                    /* addSprite(newMap, flySprite, x, y); */ }
            }
        }

        player.SetX(TileMapRenderer.TilesToPixels(3));
        player.SetY(0);
        newMap.SetPlayer(player);

        return newMap;
    }

    // Tool for "Smart" Tile placement
    // Checks neighboring tiles if they are the same as the tile placed.
    // if it is, tries to determine which texture the tile needs
    private int LookAround(ArrayList lines, char ch, int x, int y, int width, int height) {
        // Booleans for neighboring tiles
        boolean LT = false,
                RT = false,
                UP = false,
                DN = false;

        // Contractions
        boolean HZ = LT && RT,
                VT = UP && DN,
                CL = UP && !DN,
                FL = DN && !UP,
                LN = !(HZ && VT);

        // Current line scanning
        String line = (String) lines.get(y);

        // Initializing Bottom and top strings
        String lineDn = null,
                lineUp = null;

        if (y != 0)
            lineUp = (String) lines.get(y - 1);
        if (y != height - 1)
            lineDn = (String) lines.get(y + 1);

        // look around the tile (above, below, left, and right) assign the correct
        // sprite.

        if (x != 0)
            if (line.charAt(x - 1) == ch) {
                LT = true;
            }

        if (x != width - 1)
            if (line.charAt(x + 1) == ch) {
                RT = true;
            }

        if (lineUp != null)
            if (lineUp.charAt(x) == ch) {
                UP = true;
            }

        if (lineDn != null)
            if (lineDn.charAt(x) == ch) {
                DN = true;
            }

        // If is surrounded
        if (VT && HZ) {
            return 5;
        }

        // If the tile has three siblings
        if (HZ && CL) {
            return 7;
        } // is a cieling
        if (HZ && FL) {
            return 4;
        } // is a floor
        if (RT && VT && !LT) {
            return 1;
        } // is a Thick Wall
        if (LT && VT && !RT) {
            return 13;
        } // is a Thick Wall

        // If the tile has two siblings
        if (HZ && !(UP || DN)) {
            return 8;
        } // is a platform
        if (VT && !(LT || RT)) {
        } // is a pillar

        if (RT && UP && !(LT || DN)) {
            return 12;
        } // Bottom Left Corner
        if (RT && DN && !(LT || UP)) {
            return 0;
        } // Top Left Corner
        if (LT && UP && !(RT || DN)) {
            return 15;
        } // Bottom Right Corner
        if (LT && DN && !(RT || UP)) {
            return 12;
        } // Top Right Corner

        // If the tile has only 1 sibling
        if (RT && !(LT || DN || UP)) {
            return 0;
        }
        if (LT && !(RT || DN || UP)) {
            return 12;
        }
        if (DN && !(RT || LT || UP)) {
            return 4;
        }
        if (UP && !(LT || DN || RT)) {
            return 5;
        }

        // No Matches found default to normal sprite
        return 4;
    }

    private void AddRect(TileMap map, Rect hostSprite, int tileX, int tileY) {
        if (hostSprite != null) {

            // clone the sprite from the "host"
            Rect sprite = (Rect) hostSprite.Clone(
                    // center the sprite
                    (int) (TileMapRenderer.TilesToPixels(tileX)
                            + (TileMapRenderer.TilesToPixels(1) - hostSprite.GetW()) / 2),
                    // bottom-justify the sprite
                    (int) (TileMapRenderer.TilesToPixels(tileY + 1) - hostSprite.GetH()));

            // add it to the map
            map.AddRect(sprite);
        }
    }

    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------

    public void loadTileImages() {
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {

            // Currently, load rectangles
            tiles.add(new Wall(75, 75, 75, 75));
            ch++;
        }
    }

    public void LoadCreatureSprites() {
    }

    public static BufferedImage[] ReadSheet(String filename, int tileHeight, int tileWidth) {
        BufferedImage[] tileList;
        BufferedImage spritesheet = null;

        try {
            spritesheet = ImageIO.read(new File("Assets/" + filename + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sheetWidth = spritesheet.getWidth(null) / tileWidth;
        int sheetHeight = spritesheet.getHeight(null) / tileHeight;
        BufferedImage sheet = (BufferedImage) spritesheet;

        tileList = new BufferedImage[sheetWidth * sheetHeight];

        int iterator = 0;

        for (int x = 0; x < sheetWidth; x++) {
            for (int y = 0; y < sheetHeight; y++) {
                tileList[iterator] = (BufferedImage) sheet.getSubimage((x) * tileWidth, (y) * tileHeight, tileWidth,
                        tileHeight);
                iterator++;
            }
        }

        return tileList;
    }
}
