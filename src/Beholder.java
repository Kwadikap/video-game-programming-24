
// An Example Enemy, Floats around and chases the player.
// Use this enemy as a baseline for adding new ones; try to make new enemies more interesting; aiming for 16 different ones (including this and boss)
public class Beholder extends Enemy {
	// Animations
	private Animation patrol = new Animation();
	private Animation chase = new Animation();
	private Animation hurt = new Animation();
	private Animation death = new Animation();

	// Frames of Hitstun
	private final int HITSTUN = 8;
	private int currHitstun = 0;

	public Beholder(int x, int y, int width, int height) {
		super(x, y, width, height);

		this.healthbar = new Rect(x, y - 80, width, 10); // declared in the creature class
		this.healthtracker = this.healthbar.width; // declared in the creature class

		// Set the enemy to flying
		ignoreGrav = true;
		MaxSpeed = 6;

		// Initialize Animations for use
		InitializeAnimation();
	}

	public void InitializeAnimation() {
		// How to use ReadSheet: ReadSheet(Filepath (assets folder), height of sprite,
		// width of sprite, duration of animation in seconds, (optional) amount of
		// frames)
		patrol = Animation.ReadSheet("Cacodaemon/_Patrol", 64, 64, 3.0f, 6).ScaleAnimation(1.7f);
		chase = Animation.ReadSheet("Cacodaemon/_Chase", 64, 64, 3.0f, 6).ScaleAnimation(1.7f);
		hurt = Animation.ReadSheet("Cacodaemon/_Hurt", 64, 64, 1.5f, 4).ScaleAnimation(1.7f);
		death = Animation.ReadSheet("Cacodaemon/_Death", 64, 64, 2.5f).ScaleAnimation(1.7f);

		WakeUp();
	}

	public void Hurt() {
		SetState(STATE_HURT);
	}

	// Do this 60 times a second
	public void Update(float deltaTime) {
		super.Update(deltaTime);

		Animation newAnim = anim;

		// Animation Handling
		if (GetState() == STATE_NORMAL) {
			newAnim = patrol;
		}
		if (GetState() == STATE_RETREAT) {
			newAnim = patrol;
		}
		if (GetState() == STATE_CHASE) {
			newAnim = chase;
		}
		if (GetState() == STATE_DYING) {
			newAnim = death;
		}
		if (GetState() == STATE_HURT) {
			newAnim = hurt;
		}

		// Update Animations
		if (anim != newAnim) {
			anim = newAnim;
			anim.Start();
		} else {
			anim.Update(deltaTime);
		}

		// if hp is 0, die
		if (GetState() == STATE_DYING && anim.Ended()) {
			SetState(STATE_DEAD);
		}

		// When Hurt, Interrupt current state, and get knocked back a certain amount
		// before resuming
		if (GetState() == STATE_HURT) {
			// Tick the amount of frames the enemy has been stunned for
			currHitstun++;

			// After enemy has been stunned for amount specified put them back to normal.
			if (currHitstun == HITSTUN) {
				currHitstun = 0;
				SetState(STATE_NORMAL);
			}
		}

		if (GetState() != STATE_HURT) {
			// if player is not close to creature, walk around the spawn point
			if (!aggroRange.playerOverlap && GetState() != STATE_RETREAT) {
				SetState(STATE_NORMAL);
				Patrol();
			}

			// if player approaches creature, chase player
			if (aggroRange.playerOverlap && GetState() != STATE_RETREAT) {
				SetState(STATE_CHASE);
				Chase();
			}

			// if player is no longer in range, return to spawn point
			if ((GetState() == STATE_CHASE && (NearSpawnX((int) GetX())) &&
					stateTime > 120 && (NearSpawnY((int) GetY()))) ||
					GetState() == STATE_RETREAT) {
				SetState(STATE_RETREAT);
				Retreat();
			}
		}

		MovementHandler();

	}

	@Override
	public Object Clone(int x, int y) {
		Beholder clone = new Beholder((int) x, (int) y, width, height);
		return clone;
	}
}
