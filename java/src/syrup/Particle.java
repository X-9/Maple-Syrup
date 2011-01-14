package syrup;

/**
 * Collection of values to represent a single particle.
 */
public class Particle {
	/** Particle size */
	static public float r = 4f;
	
	/** Density */
	public float rho;
	
	/** Near density */
	public float rho_;
	
	/** Position */
	public Vector2D p;
	
	/** Old position */
	public Vector2D pp;
	
	/** Velocity */
	public Vector2D v;
	
	/** Accumulated particle force */
	public Vector2D f;
}