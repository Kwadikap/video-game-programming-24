import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

/* Things this Game can do:
 * Load Maps from text files
 * Place the player
 * Move the player left and right
 * Apply Gravity to the player
 * Allows player to jump
 * Scrolls through level centered on player
 * 
 * What it currently Can't do:
 * Assign Animations to the player and tiles
 * Assign Sprites to the player and tiles
 * Load a next map
 * 
 * Working on making documentation more coherent, 
 * but have projects I've been neglecting
 */

public class GameManager extends Plat2D {

	// Managers
	protected InputManager inputManager;
	protected ResourceManager resourceManager;
	protected TileMapRenderer renderer;

	protected static float delta = 0;

	// Map
	private TileMap map;

	// GameActions and input manager
	private GameAction CMD_JP,
			CMD_LT,
			CMD_RT,
			CMD_DH,
			CMD_AK;

	// Point Cache
	private Point pointCache = new Point();

	// Player
	public static Player player;

	public static void main(String[] args) {
		new GameManager().run();
	}

	// Initialization Process
	public void Init() {
		super.Init();

		// Start Input Manager
		InitInput();

		// Start Resource Manager
		resourceManager = new ResourceManager();

		// Load Resources
		renderer = new TileMapRenderer();

		try {
			renderer.SetBackground(ImageIO.read(new File("Assets/OakWoods/background/background_layer_1.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Load First Map
		map = resourceManager.LoadNextMap();
		player = map.GetPlayer();
	}

	public void Update(float deltaTime) {
		delta = deltaTime;

		// load next map if no more enemies on current map
		if (map.totalEntities() == 0) {
			map = resourceManager.LoadNextMap();
		}

		Creature player = (Creature) map.GetPlayer();

		// Player is Dead! Restart
		if (player.GetState() == Creature.STATE_DEAD) {
			soundManager.playGameOverSE();
			map = resourceManager.ReloadMap();
		}

		UpdateCreature(player, deltaTime);
		player.Update(deltaTime);

		if (player.HasChildren()) {
			Iterator<Rect> i2 = player.GetChildren();

			while (i2.hasNext()) {
				Rect child = (Rect) i2.next();
				if (child instanceof Hitbox) {
					HitboxProcess((Hitbox) child);
				}
			}
		}

		Iterator i = map.GetEntities();

		while (i.hasNext()) {
			Rect entity = (Rect) i.next();

			if (entity instanceof Creature) {
				Creature creature = (Creature) entity;
				if (creature.GetState() == Creature.STATE_DEAD) {
					map.RemRect(creature);
					i.remove();
				} else {
					UpdateCreature(creature, deltaTime);
				}
			}

			if (entity.HasChildren()) {
				Iterator i2 = entity.GetChildren();

				while (i2.hasNext()) {
					Rect child = (Rect) i2.next();

					if (child instanceof Hitbox) {
						HitboxProcess((Hitbox) child);
					}
				}
			}

			entity.Update(deltaTime);
		}

	}

	// Creation of Player Input Actions
	private void InitInput() {
		CMD_LT = new GameAction("MoveLeft");
		CMD_RT = new GameAction("MoveRight");
		CMD_JP = new GameAction("Jump", GameAction.INIT_PRESS_ONLY);
		CMD_DH = new GameAction("Dash", GameAction.INIT_PRESS_ONLY);
		CMD_AK = new GameAction("Attack", GameAction.INIT_PRESS_ONLY);

		inputManager = new InputManager(sm.GetWindow());

		inputManager.MapToKey(CMD_JP, KeyEvent.VK_UP);
		inputManager.MapToKey(CMD_LT, KeyEvent.VK_LEFT);
		inputManager.MapToKey(CMD_RT, KeyEvent.VK_RIGHT);
		inputManager.MapToKey(CMD_DH, KeyEvent.VK_SHIFT);
		inputManager.MapToKey(CMD_AK, KeyEvent.VK_Z);
	}

	// Check player inputs
	public void GetInput() {
		Player player = (Player) map.GetPlayer();

		if (player.IsAlive()) {
			player.MOV_LT = CMD_LT.IsPressed();
			player.MOV_RT = CMD_RT.IsPressed();
			player.JP_DN = (CMD_JP.GetState() == GameAction.STATE_WAITING_FOR_RELEASE);

			player.MovementHandler();

			if (CMD_JP.IsPressed()) {
				player.JumpHandler();
			}
			if (CMD_DH.IsPressed()) {
				player.DashHandler();
			}
			if (CMD_AK.IsPressed()) {
				player.AttackHandler();
			}
		}
	}

	public void Draw(Graphics2D pen) {

		renderer.Draw(pen, map, sm.GetWidth(), sm.GetHeight());
	}

	// Load in the map to draw
	public TileMap GetMap() {
		return map;
	}

	// Get point in which a collision has occurred
	public Point GetTileCollision(Rect sprite, float newX, float newY) {
		float fromX = Math.min(sprite.GetX(), newX);
		float fromY = Math.min(sprite.GetY(), newY);

		float toX = Math.max(sprite.GetX(), newX);
		float toY = Math.max(sprite.GetY(), newY);

		// get the tile locations
		int fromTileX = TileMapRenderer.PixelsToTiles(fromX);
		int fromTileY = TileMapRenderer.PixelsToTiles(fromY);

		int toTileX = TileMapRenderer.PixelsToTiles(toX + sprite.GetW() - 1);
		int toTileY = TileMapRenderer.PixelsToTiles(toY + sprite.GetH() - 1);

		// check each tile for a collision
		for (int x = fromTileX; x <= toTileX; x++) {
			for (int y = fromTileY; y <= toTileY; y++) {
				if (x < 0 || x >= map.GetWidth() || map.GetTile(x, y) != null) {
					// collision found, return the tile
					pointCache.setLocation(x, y);
					return pointCache;
				}
			}
		}

		// no collision found
		return null;
	}

	public boolean IsCollision(Rect s1, Rect s2) {
		// if the Sprites are the same, return false
		if (s1 == s2) {
			return false;
		}
		return s2.CheckCollision(s1);
	}

	public Rect GetSpriteCollision(Rect sprite) {
		// run through the list of Sprites
		Iterator i = map.GetEntities();

		while (i.hasNext()) {
			Rect otherSprite = (Rect) i.next();

			if (IsCollision(sprite, otherSprite)) {
				// collision found, return the Sprite
				return otherSprite;
			}

			// Check if rectangle owns more rectangles
			if (otherSprite.HasChildren()) {
				Iterator i2 = otherSprite.GetChildren();

				while (i2.hasNext()) {
					otherSprite = (Rect) i2.next();
					if (IsCollision(sprite, otherSprite)) {
						return otherSprite;
					}
				}
			}
		}

		if (sprite.HasChildren()) {
			Iterator i3 = sprite.GetChildren();

			while (i3.hasNext()) {
				Rect childSprite = (Rect) i3.next();

				while (i.hasNext()) {
					Rect otherSprite = (Rect) i.next();

					if (IsCollision(childSprite, otherSprite)) {
						// collision found, return the Sprite
						return otherSprite;
					}

					// Check if rectangle owns more rectangles
					if (otherSprite.HasChildren()) {
						Iterator i2 = otherSprite.GetChildren();

						while (i2.hasNext()) {
							otherSprite = (Rect) i2.next();
							if (IsCollision(otherSprite, childSprite)) {
								return otherSprite;
							}
						}
					}
				}

			}
		}
		// no collision found
		return null;
	}

	public void HitboxProcess(Hitbox hb) {
		if (hb.IsActive()) {
			Rect victim = GetSpriteCollision(hb);

			if (victim instanceof Creature) {
				Creature badguy = (Creature) victim;
				if (badguy.IsAlive()) {
					hb.CheckCollision(badguy);
				}

			}
		}
	}

	// Checks where a character is going depending on their velocities (dx, dy)
	// Handles Collisions and Applies Gravity
	public void UpdateCreature(Creature creature, float deltaTime) {

		// Checking Horizontal Collisions

		// Change X Coordinate
		float dx = creature.GetVelX();
		float oldX = creature.GetX();
		float newX = oldX + dx;

		Point tile = GetTileCollision(creature, newX, creature.GetY());

		// Apply Friction
		FrictionHandler(creature);

		if (creature instanceof Player) {
			((Player) creature).holdingWall = false;
		}

		if (tile == null) {
			creature.SetX(newX);
		} else {
			if (dx > 0) // Line up with Tile Boundary
			{
				creature.SetX(TileMapRenderer.TilesToPixels(tile.x) - creature.GetW());
			} else if (dx < 0) {
				creature.SetX(TileMapRenderer.TilesToPixels(tile.x + 1));
			}

			if (creature instanceof Player) {
				((Player) creature).holdingWall = true;
			}
			creature.CollideHorizontal();
		}

		if (creature instanceof Player) {
			CheckPlayerCollision((Player) creature, player.attack.active);
			CheckHitBoxCollision(player.attack);
		}

		// Checking Vertical Collisions

		// Change Y Coordinate
		float dy = creature.GetVelY();
		float oldY = creature.GetY();
		float newY = oldY + dy;

		tile = GetTileCollision(creature, creature.GetX(), newY);

		// Not grounded until proven otherwise
		creature.grounded = false;

		if (tile == null) {
			creature.SetY(newY);
		} else {
			if (dy > 0) {
				// Creature is proven to be grounded
				// Do something when on ground
				creature.IsOnFloor();

				// Line up with Tile Boundary
				creature.SetY(TileMapRenderer.TilesToPixels(tile.y) - creature.GetH());
			} else if (dy < 0) {
				creature.SetY(TileMapRenderer.TilesToPixels(tile.y + 1));
			}

			creature.CollideVertical();
		}

		// Apply Gravity
		if (!creature.IgnoreGravity()) {
			GravityHandler(creature);
		}

	}

	// Check for player collision with other sprites
	public void CheckPlayerCollision(Player player, boolean canKill) {
		if (!player.IsAlive()) {
			return;
		}

		Rect collisionSprite = GetSpriteCollision(player);

		if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;

			player.ReceiveDamage(badguy.damage);
			soundManager.playHitSE();

		}

	}

	public void CheckHitBoxCollision(Hitbox hitbox) {

		Rect collisionSprite = GetSpriteCollision(player);

		if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature) collisionSprite;

			soundManager.playHitSE();
			badguy.ReceiveDamage(player.damage);

			if (badguy.GetState() == Enemy.STATE_DEAD || badguy.health <= 0) {
				map.RemRect(badguy);
			}
		}

	}

	// Crunch the numbers for gravity
	public void GravityHandler(Creature creature) {
		if (creature.grounded) {
			creature.SetVelY(creature.Gravity);
		} else {
			float aerialVelocity = creature.FallAcceleration;

			if (creature instanceof Player) {
				Player player = (Player) creature;

				// If the player has let go of the space bar mid-jump, increase gravity
				if (!(player.JP_DN && player.GetVelY() > 0 && player.grounded)) {
					aerialVelocity *= player.JumpEndEarlyGravityModifier;
				}
			}

			creature.SetVelY(
					Vector2D.MoveTowards(creature.GetVelY(), creature.MaxFallSpeed, aerialVelocity * (delta - 0.99f)));
		}
	}

	// Crunch the numbers for friction
	public void FrictionHandler(Creature creature) {
		// Get Deceleration
		float deceleration;

		if (creature.grounded) {
			deceleration = creature.GroundDeceleration;
		} else {
			deceleration = creature.AirDeceleration;
		}

		if (!(creature.MOV_LT || creature.MOV_RT)) {
			// Stop moving infintesmally small amounts
			if (Math.abs(creature.GetVelX()) < 0.05f) {
				creature.SetVelX(0);
			}

			creature.SetVelX(Vector2D.Lerp(creature.GetVelX(), 0, deceleration));
		}

	}
}
