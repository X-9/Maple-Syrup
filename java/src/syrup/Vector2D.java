package syrup;

public class Vector2D {
	public float x;
	public float y;

	public Vector2D() {
		x = 0; y = 0;
	}
	
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D clone() {
		return new Vector2D(x, y);
	}
	
	public void reset() {
		x = 0; y = 0;
	}
	
	public void copy(Vector2D v) {
		x = v.x; y = v.y;
	}
	
	public void move(float x, float y) {
		this.x = x; this.y = y;
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
	
	public void scale(float s) {
		x *= s; y *= s;
	}
	
	public void devide(float d) {
		if (d == 0) {
			throw new IllegalArgumentException();
		}
		x /= d; y/= d;
	}
	
	/** Returns new dot product of two vectors. */
	public float dot(Vector2D v) {
		return x*v.x+y*v.y;
	}
	
	public void add(float x, float y) {
		this.x += x; this.y += y;
	}
	
	// Do not return this to avoid cross-reference
	public void add(Vector2D v) {
		add(v.x, v.y);
	}
	
	public void substract(float x, float y) {
		this.x -= x; this.y -= y;
	}
	
	public void substract(Vector2D v) {
		substract(v.x, v.y);
	}
	
	public void plus(Vector2D v1, Vector2D v2) {
		x = v1.x+v2.x;
		y = v1.y+v2.y;
	}
	
	public void minus(Vector2D v1, Vector2D v2) {
		x = v1.x-v2.x;
		y = v1.y-v2.y;
	}
}
