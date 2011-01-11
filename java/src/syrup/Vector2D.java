package syrup;

public class Vector2D {
	public float x;
	public float y;
	
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D clone() {
		return new Vector2D(x, y);
	}
	
	public String toString() {
		return "{x: " + x + ", y: " + y + "}";
	}
	
	public float lengthSquared() {
		return x*x+y*y;
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	/** Normalizes this vector */
	public void normalize() {
		float l = length();
		x /= l;
		y /= l;
	}
	
	/** Returns new, normalized vector */
	public Vector2D getNormalized() {
		Vector2D v = clone();
		v.normalize();
		return v;
	}
	
	/** Returns new, scaled vector. */
	public Vector2D scale(float s) {
		return new Vector2D(x*s, y*s);
	}
	
	/**Returns new, divided vector */
	public Vector2D devide(float d) {
		return new Vector2D(x/d, y/d);
	}
	
	/** Returns new dot product of two vectors. */
	public float dot(Vector2D v) {
		return x*v.x+y*v.y;
	}
	
	// Do not return this to avoid cross-reference
	public void add(Vector2D v) {
		x += v.x;
		y += v.y;
	}
	
	public void substract(Vector2D v) {
		x -= v.x;
		y -= v.y;
	}
	
	/** Returns new vector and doesn't affect this vector. */
	public Vector2D plus(Vector2D v) {
		return new Vector2D(x+v.x, y+v.y);
	}
	
	/** Returns new vector and doesn't affect this vector. */
	public Vector2D minus(Vector2D v) {
		return new Vector2D(x-v.x, y-v.y);
	}
}
