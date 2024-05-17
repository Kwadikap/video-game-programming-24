/* Class representing a 2D Vector using floating point coordinates
 * as well as functions to use those coordinates
 * Used especially for the movement of entities in video games
 * Idea borrowed from the Godot Engine:
 * https://github.com/godotengine/godot/blob/master/core/math/vector2.cpp
 */

// Simplest Use case for Vector2D: Storing and Moving Coordinates
public class Vector2D {
	float x, y;
	
	public Vector2D(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	// Taken from the unity engine https://discussions.unity.com/t/any-one-know-maths-behind-this-movetowards-function/65501/4
	public static float MoveTowards(float current, float target, float maxDelta) 
	{
		if (Math.abs(target - current) <= maxDelta) {return target;}
		return current + Math.signum(target-current) * maxDelta;
	}
	
	// Linear Interprolation
	public static float Lerp(float from, float to, float magnitude) 
	{
		return ((from * (1.0f - magnitude)) + (to * magnitude));
	}
	
}
