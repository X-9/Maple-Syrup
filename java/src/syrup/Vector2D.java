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
	
	/**
	 * Normalizes this vector
	 */
	public void normalize() {
		float l = length();
		x /= l;
		y /= l;
	}
	
	/**
	 * Returns new, normalized vector
	 */
	public Vector2D getNormalized() {
		Vector2D v = clone();
		v.normalize();
		return v;
	}
	
	/**
	 * Return new, scaled vector.
	 */
	public Vector2D scale(float s) {
		return new Vector2D(x*s, y*s);
	}
	
	// Do not return this to avoid cross-reference
	public void plus(Vector2D v) {
		x += v.x;
		y += v.y;
	}
	
	public void minus(Vector2D v) {
		x -= v.x;
		y -= v.y;
	}
	
	
}
