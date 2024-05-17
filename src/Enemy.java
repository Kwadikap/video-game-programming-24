import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Enemy extends Creature {
	// Generic Enemy Class for easy enemy implementation

	// Spawnpoint (used for enemies)
	protected Point spawnpoint;

	// States
	protected final int STATE_CHASE = 4;
	protected final int STATE_HURT = 5;
	protected final int STATE_RETREAT = 6;
	protected final int STATE_ATTACK = 7;

	// Patrol Range
	protected int range = TileMapRenderer.TilesToPixels(4);

	private boolean bump = false;
	public Rect aggroRange = new Rect(x, y, (width * 5), (height * 5));

	public Enemy(int x, int y, int width, int height) {
		super(x, y, width, height);

		AddChild(aggroRange);

		// Wherever the creature is spawned, it is it's spawnpoint.
		spawnpoint = new Point(x, y);
	}

	// Makes the enemy move left and right around their spawnpoint
	protected void Patrol() {
		MOV_UP = MOV_DN = false;

		// Move left and right
		if (GetX() >= (spawnpoint.x + (range / 2)) || bump) {
			// Invert Direction
			MOV_LT = true;
			MOV_RT = false;
			bump = false;
		} else if (GetX() <= (spawnpoint.x - (range / 2)) || bump) {
			MOV_RT = true;
			MOV_LT = false;
			bump = false;
		}
	}

	// Makes the enemy run after the player
	protected void Chase() {
		// Reset Movement
		if (IgnoreGravity())
			MOV_RT = MOV_LT = MOV_UP = MOV_DN = false;
		else
			MOV_RT = MOV_LT = false;

		if (LT_OF(GameManager.player)) {
			MOV_RT = true;
		}
		if (RT_OF(GameManager.player)) {
			MOV_LT = true;
		}

		if (IgnoreGravity()) {

			if (DN_OF(GameManager.player)) {
				MOV_UP = true;
			}
			if (UP_OF(GameManager.player)) {
				MOV_DN = true;
			}
		}
	}

	public void ReceiveDamage(int damage) {
		super.ReceiveDamage(damage);
	}

	// Makes the enemy retreat back towards their spawnpoint.
	protected void Retreat() {
		// Reset Movement
		if (IgnoreGravity())
			MOV_RT = MOV_LT = MOV_UP = MOV_DN = false;
		else
			MOV_RT = MOV_LT = false;

		// Find way back home
		if (LT_OF(spawnpoint.x)) {
			MOV_RT = true;
		}
		if (RT_OF(spawnpoint.x)) {
			MOV_LT = true;
		}

		if (IgnoreGravity()) {

			if (DN_OF(GameManager.player)) {
				MOV_UP = true;
			}
			if (UP_OF(GameManager.player)) {
				MOV_DN = true;
			}
		}

		if (x == spawnpoint.x && y == spawnpoint.y) {
			SetState(STATE_NORMAL);
		}
	}

	// Deal with Enemy Movement
	public void MovementHandler() {
		if (MOV_RT)
			SetVelX(Vector2D.Lerp(GetVelX(), MaxSpeed, Acceleration));
		if (MOV_LT)
			SetVelX(Vector2D.Lerp(GetVelX(), -MaxSpeed, Acceleration));

		if (IgnoreGravity()) {
			if (MOV_UP)
				SetVelY(Vector2D.Lerp(GetVelY(), -MaxSpeed, Acceleration));
			if (MOV_DN)
				SetVelY(Vector2D.Lerp(GetVelY(), MaxSpeed, Acceleration));
		}

		if (!(MOV_RT || MOV_LT)) {
			SetVelX(0);
		}

		if (IgnoreGravity())
			if (!(MOV_UP || MOV_DN)) {
				SetVelY(0);
			}

		MoveAggro();
		MoveHealthBar();
	}

	public void CollideHorizontal() {
		super.CollideHorizontal();
		bump = true;
	}

	public boolean NearSpawnX(int x) {
		return (x >= (spawnpoint.x + (range / 2)) || x <= (spawnpoint.x - (range / 2)));
	}

	public boolean NearSpawnY(int y) {
		return (y >= (spawnpoint.y + (range / 2)) || y <= (spawnpoint.y - (range / 2)));
	}

	private void MoveAggro() {
		aggroRange.SetX(x + (width / 2) - (aggroRange.width / 2));
		aggroRange.SetY(y + (height / 2) - (aggroRange.height / 2));
	}

	// Return if an entity is within the entity's aggresive range
	public boolean InRange() {
		return aggroRange.playerOverlap;
	}

	// Finding something relative to the Enemy
	public boolean RT_OF(Rect neighbor) {
		return neighbor.x + neighbor.width / 2 < x;
	}

	public boolean LT_OF(Rect neighbor) {
		return x + width / 2 < neighbor.x;
	}

	public boolean UP_OF(Rect neighbor) {
		return y + height / 2 < neighbor.y;
	}

	public boolean DN_OF(Rect neighbor) {
		return neighbor.y + neighbor.height / 2 < y;
	}

	// Finding a point relative to the Enemy
	public boolean RT_OF(float destination) {
		return destination < x;
	}

	public boolean LT_OF(float destination) {
		return destination > x;
	}

	public boolean UP_OF(float destination) {
		return destination > y;
	}

	public boolean DN_OF(float destination) {
		return destination < y;
	}

	public void Draw(Graphics pen, float x, float y) {
		// super.Draw(pen, x, y);
		// aggroRange.Draw(pen, x, y);
		healthbar.Draw(pen, x, y);
		pen.setColor(Color.GREEN);
		healthbar.DrawFull(pen, x, y, healthtracker, healthbar.height);
		pen.setColor(Color.BLACK);

	}

	@Override
	public Object Clone(int x, int y) {
		return null;
	}

}
