import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Player extends Creature {

	// Extended Physics numbers for player
	public static final float JumpBuffer = .2f;
	public static final float CoyoteTime = .15f;

	// Dash Numbers
	private final float DashSpeed = MaxSpeed * 1.5f;
	private final float DashReset = 3;
	private float DashCooldown = 3;

	// Booleans for dashing
	private boolean canDash = true,
			isDashing = false;

	// Boolean for Attacking
	private boolean canAttack = true;
	private boolean firstStrike = false;

	private float attackCooldown = 3.0f;
	private final float attackReset = 3.0f;

	// Player States
	public static final int STATE_JUMPING = 4;
	public static final int STATE_ATTACKING = 5;
	public static final int STATE_ROLL = 6;

	// Jump Handling
	private float time;
	private float timeJumpWasPressed;

	// Booleans for Jumping
	public boolean JP_DN = false,
			bufferedJumpUsable = false;

	// Booleans for Wall Jumping
	public boolean holdingWall = false;

	// Booleans for Jump Buffering
	public final boolean hasBufferedJump = (bufferedJumpUsable && time < timeJumpWasPressed + Player.JumpBuffer);
	public final boolean coyoteUsable = (bufferedJumpUsable && time < timeJumpWasPressed + Player.JumpBuffer);

	Hitbox attack = new Hitbox(x - 80, y, 80, height, this, this.damage, 38);

	// Player Animations
	Animation idle = new Animation();
	Animation run = new Animation();
	Animation roll = new Animation();
	Animation atk1 = new Animation();
	Animation atk2 = new Animation();
	Animation die = new Animation();
	Animation hurt = new Animation();

	public Player(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.health = 10;
		AddChild(attack);
		InitializeAnimation();
	}

	// Define Animations
	public void InitializeAnimation() {
		// How to use ReadSheet: ReadSheet(Filepath (assets folder), height of sprite,
		// width of sprite, duration of animation in seconds)
		idle = Animation.ReadSheet("FreeKnight/_Idle", 80, 120, 3.0f).ScaleAnimation(1.7f);
		run = Animation.ReadSheet("FreeKnight/_Run", 80, 120, 3.0f).ScaleAnimation(1.7f);
		roll = Animation.ReadSheet("FreeKnight/_Roll", 80, 120, 1.5f).ScaleAnimation(1.7f);
		atk1 = Animation.ReadSheet("FreeKnight/_Attack", 80, 120, 2.5f).ScaleAnimation(1.7f);
		atk2 = Animation.ReadSheet("FreeKnight/_Attack2", 80, 120, 2.5f).ScaleAnimation(1.7f);
		die = Animation.ReadSheet("FreeKnight/_Death", 80, 120, 2.5f).ScaleAnimation(1.7f);
		hurt = Animation.ReadSheet("FreeKnight/_Fall", 80, 120, 2.5f).ScaleAnimation(1.7f);
	}

	// Animation & State Updating
	public void Update(float deltaTime) {
		super.Update(deltaTime);

		// Animation Changing
		Animation newAnim = anim;
		boolean moving = MOV_LT || MOV_RT;

		if (state == STATE_NORMAL && !moving) {
			newAnim = idle;
		}
		if (state == STATE_NORMAL && moving) {
			newAnim = run;
		}
		if (state == STATE_ATTACKING && firstStrike == false) {
			newAnim = atk1;
		}
		if (state == STATE_ATTACKING && firstStrike == true) {
			newAnim = atk2;
		}
		if (state == STATE_ROLL) {
			newAnim = roll;
		}
		if (state == STATE_DYING) {
			newAnim = die;
		}
		if (state == STATE_HURT) {
			newAnim = hurt;
		}

		// Update Player Animations
		if (anim != newAnim) {
			anim = newAnim;
			anim.Start();
		} else {
			anim.Update(deltaTime);
		}

		// Player state updating
		if (state == STATE_ROLL) {
			ExecuteDash();
		}

		// Stop Dashing
		if (state == STATE_ROLL && anim.Ended()) {
			// Re-Enable Gravity
			isDashing = false;
			ignoreGrav = false;

			ResetState();

			// Reset all Variables, allow Dashing once more
			DashCooldown = DashReset;
			canDash = true;
		}

		// Stop Attacking
		if (state == STATE_ATTACKING && anim.Ended()) {
			attack.Deactivate();
			ResetState();

			canAttack = true;
			attackCooldown = attackReset;

			firstStrike = !firstStrike;
		}

		if (state == STATE_HURT) {

		}
		// End hurt state
		if (state == STATE_HURT && anim.Ended()) {
			ResetState();
		}

		if (state == STATE_DYING && anim.Ended()) {
			SetState(STATE_DEAD);
		}

		// Cooldowns and buffering
		if (DashCooldown >= 0) {
			DashCooldown -= GameManager.delta;
		}
		if (attackCooldown >= 0) {
			attackCooldown -= GameManager.delta - .88;
		}

		// Moving the Hitbox
		if (direction > 0) {
			attack.SetX(x + width);
		}
		if (direction < 0) {
			attack.SetX(x - 80);
		}
		attack.SetY(y);
	}

	// Do something if the player is on the floor
	public void IsOnFloor() {
		super.IsOnFloor();
	}

	// Handling behind player acceleration and deceleration
	public void MovementHandler() {
		if (state == STATE_NORMAL) {
			if (MOV_RT)
				SetVelX(Vector2D.Lerp(GetVelX(), MaxSpeed, Acceleration));
			if (MOV_LT)
				SetVelX(Vector2D.Lerp(GetVelX(), -MaxSpeed, Acceleration));
		}
		MoveHealthBar();
	}

	// Check if can Attack
	public void AttackHandler() {
		if (state != STATE_ROLL && canAttack) {
			ExecuteAttack();
		}

	}

	// Check if can jump
	public void JumpHandler() {
		// The player can jump
		if (grounded || holdingWall) {
			ExecuteJump();
		}
	}

	// Check if can dash
	public void DashHandler() {
		if (canDash && DashCooldown <= 0) {
			canDash = false;
			StateMachine(STATE_ROLL);
			ignoreGrav = true; // Turn off Gravity when Dashing
		}
	}

	// Dashing Function
	public void ExecuteDash() {
		if (!grounded)
			SetVelY(0);
		SetVelX(DashSpeed * direction);
	}

	// Jumping Function
	public void ExecuteJump() {
		timeJumpWasPressed = 0;
		bufferedJumpUsable = false;

		// Wall-Jumping
		if (holdingWall) {
			SetVelX(-(JumpPower / 2 * direction));
		}

		grounded = false;
		SetVelY(-JumpPower);
	}

	// Attacking Function
	public void ExecuteAttack() {
		state = STATE_ATTACKING;

		attack.Activate();

	}

	public void respawn() {
		health = 18;
		healthtracker = healthbar.width;
		ResetState();
	}

	public void StateMachine(int state) {
		if (GetState() == STATE_NORMAL) {
			SetState(state);
		}
	}

	public void ResetState() {
		SetState(STATE_NORMAL);
	}

	public int GetState() {
		return state;
	}

	public void SetState(int state) {
		this.state = state;
	}

	@Override
	public Object Clone(int x, int y) {
		Player clone = new Player((int) x, (int) y, width, height);
		return clone;
	}

	public void Draw(Graphics pen, float x, float y) {
		// super.Draw(pen, x, y);
		// attack.Draw(pen, x, y);
		healthbar.Draw(pen, x, y);

		// update color of healthbar based on healthtracker
		pen.setColor(Color.GREEN);

		healthbar.DrawFull(pen, x, y, healthtracker, healthbar.height);
		pen.setColor(Color.BLACK);

	}

}
