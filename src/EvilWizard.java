import java.awt.Graphics;

public class EvilWizard extends Enemy {
	// states
	public static final int STATE_JUMPING = 4;
	public static final int STATE_ATTACKING = 5;

	// Boolean for attacking
	private boolean firstStrike = false;

	Hitbox attack = new Hitbox(x - 80, y, 80, height, this, this.damage, 38);

	// Animations
	private Animation AN_ATK1 = new Animation();
	private Animation AN_ATK2 = new Animation();
	private Animation AN_DEAD = new Animation();
	private Animation AN_WALK = new Animation();
	private Animation AN_HURT = new Animation();
	private Animation AN_JUMP = new Animation();
	private Animation AN_IDLE = new Animation();

	// Frames of Hitstun
	private final int HITSTUN = 8;
	private int currHitstun = 0;

	public EvilWizard(int x, int y, int width, int height) {
		super(x, y, width, height);
		MaxSpeed = 10;

		// Initialize Animations for use
		InitializeAnimation();
	}

	public void InitializeAnimation() {
		// How to use ReadSheet: ReadSheet(Filepath (assets folder), height of sprite,
		// width of sprite, duration of animation in seconds)
		AN_ATK1 = Animation.ReadSheet("EvilWizard/Attack1", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_ATK2 = Animation.ReadSheet("EvilWizard/Attack2", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_DEAD = Animation.ReadSheet("EvilWizard/Death", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_WALK = Animation.ReadSheet("EvilWizard/Run", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_HURT = Animation.ReadSheet("EvilWizard/Hurt", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_JUMP = Animation.ReadSheet("EvilWizard/Jump", 168, 250, 3.0f).ScaleAnimation(1.7f);
		AN_IDLE = Animation.ReadSheet("EvilWizard/Idle", 168, 250, 3.0f).ScaleAnimation(1.7f);

		WakeUp();
	}

	// Do this 60 times a second
	public void Update(float deltaTime) {
		super.Update(deltaTime);

		Animation newAnim = anim;
		boolean moving = (MOV_LT || MOV_RT || MOV_UP || MOV_DN);

		// Animation Handling
		if (GetState() == STATE_NORMAL) {
			if (!moving) {
				newAnim = AN_IDLE;
			} else
				newAnim = AN_WALK;
		}

		if (GetState() == STATE_NORMAL) {
			newAnim = AN_WALK;
		}
		if (GetState() == STATE_RETREAT) {
			newAnim = AN_WALK;
		}
		if (GetState() == STATE_CHASE) {
			newAnim = AN_WALK;
		}
		if (GetState() == STATE_DYING) {
			newAnim = AN_DEAD;
		}
		if (GetState() == STATE_HURT) {
			newAnim = AN_HURT;
		}
		if (state == STATE_ATTACKING && firstStrike == false) {
			newAnim = AN_ATK1;
		}
		if (state == STATE_ATTACKING && firstStrike == true) {
			newAnim = AN_ATK2;
		}

		// Update Animations
		if (anim != newAnim) {
			anim = newAnim;
			anim.Start();
		} else {
			anim.Update(deltaTime);
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

	public void Draw(Graphics pen, float x, float y) {
		super.Draw(pen, x, y);
		aggroRange.Draw(pen, x, y);
	}

	@Override
	public Object Clone(int x, int y) {
		EvilWizard clone = new EvilWizard((int) x, (int) y, width, height);
		return clone;
	}
}
