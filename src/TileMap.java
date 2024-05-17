
import java.awt.Image;
import java.util.Iterator;
import java.util.LinkedList;

/* Tilemap Class contains the data for a tile-based map,
 * Inlcuding sprites. Each tile is a reference to a rectangle.
 */

public class TileMap {
	private Rect[][] tiles;
	private LinkedList<Rect> entities;
	private Player player;

	public TileMap(int width, int height) {
		tiles = new Rect[width][height];
		entities = new LinkedList<Rect>();
	}

	public Rect GetTile(int x, int y) {
		if (x < 0 || x >= GetWidth() ||
				y < 0 || y >= GetHeight()) {
			return null;
		} else {
			return tiles[x][y];
		}
	}

	// Place Rectangles in the meantime
	public void SetTile(int x, int y, Rect rect) {
		tiles[x][y] = rect;
	}

	public Player GetPlayer() {
		return player;
	}

	public void SetPlayer(Player player) {
		this.player = player;
	}

	public void AddRect(Rect sprite) {
		entities.add(sprite);
	}

	public void RemRect(Rect sprite) {
		entities.remove(sprite);
	}

	public Iterator<Rect> GetEntities() {
		return entities.iterator();
	}

	public int totalEntities() {
		return entities.size();
	}

	public int GetWidth() {
		return tiles.length;
	}

	public int GetHeight() {
		return tiles[0].length;
	}

}
