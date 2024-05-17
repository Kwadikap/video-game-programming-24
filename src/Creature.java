import java.awt.Graphics2D;

/* Abstract class that defines what a creature is,
 * creatures are entities which can move around,
 * and are effected by physics unless otherwise stated
 */
public abstract class Creature extends Rect {

	// Physics TODO: Refactor to be less ugly

	// Statistics
	// default 3 hp, 1 damage
	public int health = 3;
	public int damage = 1;

	// Ground Speed Handling
	public float MaxSpeed = 14; // top speed an entity can achieve
	public final float Acceleration = .25f;
	public final float GroundDeceleration = Acceleration / 2;

	// Air Speed Handling
	public final float Gravity = 1.5f;
	public final float AirDeceleration = GroundDeceleration / 2;
	public final float JumpPower = 36;
	public final float JumpEndEarlyGravityModifier = 3;

	// Fall Handling
	public final float MaxFallSpeed = 40;
	public final float FallAcceleration = 110;

	// Get if creature is attempting to move
	public boolean MOV_LT, MOV_RT, MOV_UP, MOV_DN;

	// Generic states in which a creature can remain in
	public static final int STATE_NORMAL = 0;
	public static final int STATE_HURT = 1;
	public static final int STATE_DYING = 2;
	public static final int STATE_DEAD = 3;
	// add state hurt

	// Character States
	protected int state;
	protected long stateTime;

	// For Character Jumping
	public boolean grounded;

	// Ignore Gravity
	protected boolean ignoreGrav;

	// -1 left, +1 right
	protected int direction = -1;

	// rect that is displayed above players head representing player's health
	public Rect healthbar = new Rect(x, y - 80, health * 10, 10);

	// Is used to create the illusion of the healthbar decreasing
	protected int healthtracker = healthbar.width;

	// Constructor
	public Creature(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	// overrided by subsclasses
	public void InitializeAnimation() {
	}

	protected void MoveHealthBar() {
		healthbar.SetX(x + (width / 2) - (healthbar.width / 2));
		healthbar.SetY(y - 50 + (height / 2) - (healthbar.height / 2));
	}

	// Process Damage Taken
	public void ReceiveDamage(int damage) {
		healthtracker -= damage * 10;

		if (healthtracker < 0) {
			healthtracker = 0;
		}

		this.health -= damage;

		SetState(STATE_HURT);
		PushBack();

		if (health <= 0) {
			SetState(STATE_DYING);
		}
	}

	// push back creature in the opposite direction from where its facing
	public void PushBack() {
		this.oldX = x;

		if (direction > 0) {
			this.x -= this.width * 2;
		}

		if (direction < 0) {
			this.x += this.width * 2;
		}
	}

	// Start Moving Left
	public void WakeUp() {

		if (GetState() == STATE_NORMAL && GetVelX() == 0) {
			MOV_LT = true;
		}
	}

	public void Update(float deltaTime) {

		if (GetVelX() > 0 || GetVelX() < 0) {
			direction = (int) Math.signum(GetVelX());
		}

		stateTime += deltaTime;

		// Animation flipping
		if (anim != null) {

			if (direction == -1 && !anim.IsMirrored()) {
				anim.MirrorAnimation();
			} else if (direction == 1 && anim.IsMirrored()) {
				anim.MirrorAnimation();
			}
		}
	}

	// Object Cloning
	public abstract Object Clone(int x, int y);

	// Do something when on the ground
	public void IsOnFloor() {
		grounded = true;
	}

	public boolean IgnoreGravity() {
		return ignoreGrav;
	}

	// Draw the spirte of the character
	public void DrawSprite(Graphics2D pen, float offsetX, float offsetY) {
		pen.drawImage(anim.GetImage(), (int) offsetX, (int) offsetY, null);
	}

	// Collisions
	public void CollideHorizontal() {
		SetVelX(0);
	}

	public void CollideVertical() {
		SetVelY(Gravity);
	}

	// Announce & Set State
	public void SetState(int state) {

		if (this.state != state) {
			this.state = state;
			stateTime = 0;

			if (state == STATE_DYING) {
				// Stop the creature from moving
				SetVelX(0);
				SetVelY(0);
			}
		}
	}

	public int GetState() {
		return state;
	}

	public boolean IsAlive() {
		return state == STATE_NORMAL;
	}

	public float GetMaxSpeed() {
		return 0;
	}

}
