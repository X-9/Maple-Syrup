package syrup;

import java.util.Collection;
import java.util.Random;

abstract class Emitter {
	static private final int N = 50;
	static private final float spread = 20f;
	
	static private float rand(float a, float b) {
		return a + (new Random().nextFloat())*(b+1-a);
	}
	
	static public void emit(Collection<Particle> col, float x, float y) {
		for (int i = 0; i < N; ++i) {
			Particle p = new Particle();
			p.p = new Vector2D(rand(x-(spread/2), x+(spread/2)), rand(y-(spread/2), y+(spread/2)));
			p.v = new Vector2D(rand(-1,1), rand(-1,1));
			col.add(p);
		}
	}
}

public class Particle {
	static public float r = 4f;	// particle size
	
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
	
	public Vector2D f;
}