/* Abstracts user-initiated actions, like jumping and moving.
 * Gameactions can be mapped to keys, or mice.
 */


public class GameAction {
	
	// Normal Behavior, returns true as long as the key is held down.
	public static final int NORMAL = 0;
	
	// Initial press behavior, returns true at the moment the key is pressed. 
	// Does not return true until it is released and pressed again
	public static final int INIT_PRESS_ONLY = 1;
	
	public static final int STATE_RELEASED = 0;
	public static final int STATE_PRESSED = 1;
	public static final int STATE_WAITING_FOR_RELEASE = 2;
	
	// Attributes
	private String name;
	private int behavior;
	private int amt;
	private int state;
	
	// Create a new GameAction with the NORMAL behavior
	public GameAction(String name) { this(name, NORMAL); }
	
	// Create GameAction with specified behavior
	public GameAction(String name, int behavior) 
	{
		this.name = name;
		this.behavior = behavior;
		Reset();
	}
	
	public void Reset() 
	{
		state = STATE_RELEASED;
		amt = 0;
	}
	
	public synchronized void Tap() 
	{
		Press();
		Release();
	}
	
	// Signals key was pressed
	public synchronized void Press() { Press(1); }
	
	// Signals the key was pressed a specified number of times
	// or that the mouse moved a specified distance.
	public synchronized void Press(int amt) 
	{
		if (state != STATE_WAITING_FOR_RELEASE) 
		{
			this.amt += amt;
			state = STATE_PRESSED;
		}
	}
	
	public synchronized int GetAmt() 
	{
		int retVal = amt;
		if (retVal != 0)
		{
			if (state == STATE_RELEASED) { amt = 0; }
			else
			if (behavior == INIT_PRESS_ONLY)
			{
				state = STATE_WAITING_FOR_RELEASE;
				amt = 0;
			}
		}
		return retVal;
	}
	
	public synchronized boolean IsPressed() { return ( GetAmt() != 0 ); }
	public synchronized void 	Release() 	{ state = STATE_RELEASED; }
	
	public int 	  GetState() { return state; }
	public String GetName()  { return name; }
	
}
