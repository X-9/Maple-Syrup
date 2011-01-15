package syrup;

import java.awt.Dimension;
import java.util.Random;


/**
 * Based on:
 * 
 * [1] Matthias Müller, David Charypar, Markus Gross. 'Particle-Based Fluid 
 *     Simulation for Interactive Applications.', 2003.
 *     
 * [2] Simon Clavet, Philippe Beaudoin, Pierre Poulin. 'Particle-based 
 *     Viscoelastic Fluid Simulation', 2005.
 * 
 */
public class Liquid implements Idle {
	private static final long serialVersionUID = 1L;

	private Vector2D G = new Vector2D(0, .06f);		// gravity
	private float h = 10.f;							// interaction radius
	private float hh = h*h;							// powered radius
	private float rho0 = 10f;						// rest density
	private float k = .004f;						// stiffness
	private float k_ = k*10f;						// yet another parameter
	private float sigma = 0f;						// sigma
	private float beta = .3f;						// beta
	
	private final SpatialTable<Particle> particles;
	private Vector2D attractor;
	
	
	// Getters and setters
	public void setGravityX(float gravity) { this.G.x = gravity; }
	public void setGravityY(float gravity) { this.G.y = gravity; }
	public void setRadius(float radius) { this.h = radius; this.hh = h*h;}
	public void setDensity(float density) {	this.rho0 = density; }
	public void setStiffness(float stiffness) {	this.k = stiffness; this.k_ = k*10f; }
	public void setSigma(float sigma) {	this.sigma = sigma;	}
	public void setBeta(float beta) { this.beta = beta;	}
	public void setAttractor(Vector2D p) { this.attractor = p; }
	
	// Source
	public Liquid(final SpatialTable<Particle> table) {
		if (table == null) {
			throw new IllegalArgumentException("SpatialTable instance in null");
		}
		particles = table;
		
		attractor = new Vector2D(-1, -1); // mouse out of liquid world
	}
	
	private static float rand() {
		return new Random().nextFloat()*2-1;
	}
	
	public Dimension getSize() {
		return new Dimension(200, 300);
	}
	
	public void populate() {
		
		Dimension size = getSize();
		int step = 5, i = 0;
		int N = 1500;
		for (int x = 30; x < size.width; x += step) {
			for (int y = 30; y < size.height && N > 0; y += step, ++i) {
				Particle p = new Particle();
				p.p = new Vector2D(x, y);
				p.pp = new Vector2D(x+rand(), y+rand());
				p.f = new Vector2D(0, 0);
				p.v = p.p.minus(p.pp);
				particles.add(p);
				N--;
			}
		}
		
		System.out.println("Populated: " + i + " particles");
	}
	
	/**
	 * Checks if particle collides with a wall. Modify particle force if
	 * it collide.
	 * 
	 * @param p Particle to check.
	 */
	private void wallCollision(Particle p) {
		// Liquid dimencion
		Dimension size = getSize();
		
		// Add small padding to liquid size, because
		// particle position identify upper left corner of particle
		size.width 	-= Particle.r;
		size.height -= Particle.r;

		if (p.p.x > size.width) {
			p.f.substract(new Vector2D((p.p.x-size.width)/2, 0));
		}
		
		if (p.p.x < 20) {
			p.f.add(new Vector2D((20-p.p.x)/2, 0));
		}
		
		if (p.p.y > size.height) {
			p.f.substract(new Vector2D(0, (p.p.y-size.height)/2));
		}
		
		if (p.p.y < 20) {
			p.f.add(new Vector2D(0, (20-p.p.y)/2));
		}
	}

	/**
	 * Attract particle to mouse cursor.
	 * 
	 * @param p Particle to attract.
	 */
	public void attract(Particle p) {
		final int rsqrd = 2500;	// squared size of attraction length
		final float k = .005f;	// coefficient of attraction
		
		Dimension size = getSize();
		
		// Do nothing if user moved cursor position out of world size
		if (attractor.x < 0 || attractor.x > size.width)  return;
		if (attractor.y < 0 || attractor.y > size.height) return;
		
		if (attractor.minus(p.p).lengthSquared() < rsqrd) {
			p.f.add(attractor.minus(p.p).scale(k));	
		}
	}
	
	@Override
	public void move() {
		// apply viscosity
		viscosity();
		
		for (Particle p : particles) {
			p.pp = p.p.clone();			// save previous position
			p.p.add(p.v);				// apply force and velocity
			p.p.add(p.f);
			
			p.f = G.clone();		 	// reset force with gravity
			p.v = p.p.minus(p.pp); 		// compute next velocity
			
			wallCollision(p);
			
			attract(p);					// attract particles to mouse cursor
		}
		
		particles.rehash();
		
		// double density relaxation
		density();
	}
	
	private void viscosity() {
		for (Particle pi : particles) {
			pi.rho = 0;
			pi.rho_ = 0;
			
			for (Particle pj : particles.nearby(pi)) {
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.lengthSquared();
				
				if (q < hh && q != 0 ) {					
					q = (float)Math.sqrt(q);	// q is length	
					rij = rij.devide(q);		// now rij is normalized
					q /= h;						// find q

					float qq = (1-q)*(1-q);
					pi.rho += qq;
					pi.rho_+= qq*(1-q);
					
					Vector2D vij = pi.v.minus(pj.v);
					float u = vij.dot(rij);

					if (u > 0) {
						float s = (1-q)*(sigma*u+beta*u*u);
						Vector2D I = rij.scale(s*.5f);
						pi.v.substract(I);
						pj.v.add(I);
					}
				}
			} // for
		}
	}
	
	private void density() {
		for (Particle pi : particles) {
			
			float P  = k*(pi.rho - rho0);
			float P_ = k_*pi.rho_;
			
			Vector2D dx = new Vector2D(0, 0);
			
			for (Particle pj : particles.nearby(pi)) {
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.lengthSquared();
				if (q < hh && q != 0) {
					q = (float)Math.sqrt(q);
					rij = rij.devide(q);
					q /= h;
					float a = P*(1-q)+P_*(1-q)*(1-q);
					Vector2D uij = rij.scale(a).scale(0.5f);
					pj.f.add(uij);
					dx.substract(uij);
				}
			} // for
			pi.f.add(dx);
		}
	}
}