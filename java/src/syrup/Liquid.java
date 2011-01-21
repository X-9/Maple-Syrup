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

	private float G = .06f;							// absolute gravity
	private Vector2D g = new Vector2D(0, G);		// gravity vector
	private float h = 10.f;							// interaction radius
	private float hh = h*h;							// powered radius
	private float rho0 = 10f;						// rest density
	private float k = .004f;						// stiffness
	private float k_ = k*10f;						// yet another parameter
	private float sigma = 0f;						// sigma
	private float beta = .3f;						// beta
	private int n = 1500;							// number of particles
	private int hpadding = 20;						// keep particles away from border
	private int vpadding = 20;
	
	private final SpatialTable<Particle> particles;
	private Vector2D attractor;
	private Vector2D emitter;
	
	
	// Getters and setters
	public void setGravity(float gravity) { this.G = gravity; }
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
		emitter = new Vector2D(-1, -1);
	}
	
	public void turnGravity(float radians) {
		float gx = (float) (G*Math.sin(radians));
		float gy = (float) (G*Math.cos(radians));
		
		g.move(gx, gy);
	}
	
	private static float rand() {
		return new Random().nextFloat()*2-1;
	}
	
	public Dimension getSize() {
		return new Dimension(200, 300);
	}
	
	public void beginEmit(float x, float y) {
		emitter.move(x, y);
	}
	
	public void endEmit() {
		emitter.move(-1, -1);
	}
	
	private void populate() {
		if (n < 0) return;
		
		Dimension size = getSize();
		
		// Do nothing if user moved cursor position out of world size
		if (emitter.x < hpadding || emitter.x > size.width-hpadding)  return;
		if (emitter.y < vpadding || emitter.y > size.height-vpadding) return;
		
		// generate 10 paticles
		for (int i = 0; i < 10; ++i, --n) {
			Particle p = new Particle();
			p.p = new Vector2D(emitter.x+rand()*10, emitter.y+rand()*10);
			p.pp = p.p.clone();
			p.f = new Vector2D(0, 0);
			p.v = new Vector2D(0, 0);
			p.v.minus(p.p, p.pp);
			particles.add(p);
		}
	}
	
	/**
	 * Checks if particle collides with a wall. Modify particle force if
	 * it collide.
	 * 
	 * @param p Particle to check.
	 */
	private void wallCollision(Particle p) {
		// Liquid dimension.
		Dimension size = getSize();
		
		// opposite force coefficient [0, n), bigger number gives less strong force 
		float k = 2;

		if (p.p.x > size.width-hpadding) {
			p.f.substract((p.p.x-(size.width-hpadding))/k, 0);
		}
		
		if (p.p.x < hpadding) {
			p.f.add((hpadding-p.p.x)/k, 0);
		}
		
		if (p.p.y > size.height-vpadding) {
			p.f.substract(0, (p.p.y-(size.height-vpadding))/k);
		}
		
		if (p.p.y < vpadding) {
			p.f.add(0, (vpadding-p.p.y)/k);
		}
	}

	/**
	 * Attract particle to mouse cursor.
	 * 
	 * @param p Particle to attract.
	 */
	private void attract(Particle p) {
		final int rsqrd = 2500;	// squared size of attraction length
		final float k = .005f;	// coefficient of attraction
		
		Dimension size = getSize();
		
		// Do nothing if user moved cursor position out of world size
		if (attractor.x < hpadding || attractor.x > size.width-hpadding)  return;
		if (attractor.y < hpadding || attractor.y > size.height-hpadding) return;
		
		float dx = attractor.x-p.p.x;
		float dy = attractor.y-p.p.y;
		float sqlen = (dx*dx)+(dy*dy);
		if (sqlen < rsqrd) {
			p.f.add(dx*k, dy*k);	
		}
	}

	//@Override
	public void move() {
		// add new particles
		populate();
		
		// apply viscosity
		viscosity();
		
		for (Particle p : particles) {
			p.pp.copy(p.p);				// save previous position
			p.p.add(p.v);				// apply force and velocity
			p.p.add(p.f);
			
			p.f.copy(g);			 	// reset force with gravity
			
			p.v.minus(p.p, p.pp);		// compute next velocity
			
			wallCollision(p);
			
			attract(p);					// attract particles to mouse cursor
		}
		
		particles.rehash();
		
		// double density relaxation
		density();
	}
	
	private void viscosity() {
		Vector2D rij = new Vector2D();
		Vector2D vij = new Vector2D();
		
		for (Particle pi : particles) {
			pi.rho = 0;
			pi.rho_ = 0;
			
			for (Particle pj : particles.nearby(pi)) {
				rij.minus(pj.p, pi.p);
				float q = rij.lengthSquared();
				
				if (q < hh && q != 0 ) {
					q = (float)Math.sqrt(q);				// q is length
					rij.devide(q);							// now rij is normalized
					q /= h;									// find q

					float qq = (1-q)*(1-q);
					pi.rho += qq;
					pi.rho_+= qq*(1-q);
					
					vij.minus(pi.v, pj.v);
					float u = vij.dot(rij);

					if (u > 0) {
						float s = (1-q)*(sigma*u+beta*u*u);
						rij.scale(s*.5f);	// I
						pi.v.substract(rij);
						pj.v.add(rij);
					}
				}
			} // for
		}
	}
	
	private void density() {
		Vector2D rij = new Vector2D();
		Vector2D dx = new Vector2D();
		
		for (Particle pi : particles) {
			
			float P  = k*(pi.rho - rho0);
			float P_ = k_*pi.rho_;
			
			dx.reset();
			
			for (Particle pj : particles.nearby(pi)) {
				rij.minus(pj.p, pi.p);
				float q = rij.lengthSquared();
				if (q < hh && q != 0) {
					// try to minimise amount of operations.
					q = (float)Math.sqrt(q);
					rij.devide(q);
					q /= h;
					float a = P*(1-q)+P_*(1-q)*(1-q);
					rij.scale(a*.5f);	// u_{ij}
					pj.f.add(rij);
					dx.substract(rij);
				}
			} // for
			pi.f.add(dx);
		}
	}
}