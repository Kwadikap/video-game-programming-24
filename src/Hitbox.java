import java.awt.Color;
import java.awt.Graphics;

public class Hitbox extends Rect{
	Rect owner;
	int damage;
	float knockback = 0;
	
	boolean active = false;
	
	public Hitbox(float x, float y, int width, int height, Rect owner, int damage) 
	{
		super(x, y, width, height);
		this.owner  = owner;
		this.damage = damage;
	}
	
	public Hitbox(float x, float y, int width, int height, Rect owner, int damage, float knockback) 
	{
		super(x, y, width, height);
		this.owner  = owner;
		this.damage = damage;
		this.knockback = knockback;
	}
	
	public boolean CheckCollision(Rect neighbor) 
	{
		if (neighbor instanceof Creature && neighbor != owner) 
		{
			Creature creature = (Creature) neighbor;
			creature.ReceiveDamage(damage);
		}
		
		
		return super.CheckCollision(neighbor);
	}
	
	// Override-able for Collision Handling
	public void OnCollisionLT(Rect neighbor)
	{
		if (neighbor instanceof Creature && neighbor != owner) 
		{
			Creature creature = (Creature) neighbor;
			creature.SetVelX( -(knockback) );
		}
		
	}
	public void OnCollisionRT(Rect neighbor)
	{
		if (neighbor instanceof Creature && neighbor != owner) 
		{
			Creature creature = (Creature) neighbor;
			creature.SetVelX( (knockback) );
		}
		
	}
	public void OnCollisionDN(Rect neighbor)
	{
		if (neighbor instanceof Creature && neighbor != owner) 
		{
			Creature creature = (Creature) neighbor;
			creature.SetVelX( (knockback / 4) );
		}
		
	}
	
	public void OnCollisionUP(Rect neighbor)
	{
		if (neighbor instanceof Creature && neighbor != owner) 
		{
			Creature creature = (Creature) neighbor;
			creature.SetVelX( -(knockback / 4) );
		}
		
	}
	
	public void UpdateKnockback(int kb) { knockback = kb; }
	public void Activate()    { active = true; }
	public void Deactivate()  { active = false;}
	public boolean IsActive() { return active; }
	
	// Draw the rectangle with an offset
	public void Draw(Graphics pen) 
	{
		if (active) 
		{
			pen.setColor(Color.RED);
			super.Draw(pen);
		}
	}
	
	// Draw the rectangle with an offset
	public void Draw(Graphics pen, int x, int y) 
	{
		if (active) 
		{
			pen.setColor(Color.RED);
			super.Draw(pen, x, y);
		}
	}	
	
	
}
