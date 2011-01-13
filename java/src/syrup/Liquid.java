package syrup;

import java.awt.Dimension;
import java.util.ArrayList;
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

	private float G = .06f;		// gravity
	private float h = 10.f;		// interaction radius
	private float hh = h*h;		// powered radius
	private float rho0 = 10f;	// rest density
	private float k = .004f;	// stiffness
	private float k_ = .1f;		// yet another parameter
	private float sigma = 0f;	// sigma
	private float beta = .3f;	// beta
	
	private SpatialTable<Particle> particles;
	private ArrayList<Particle>[] nearby = new ArrayList[3000];
	private Vector2D attractor;
	
	
	// Getters and setters
	public void setGravity(float gravity) {	
		this.G = gravity; 
		int min = 10000000, max = -10000000;
		for (Particle p : particles) {
			int key = particles.generate(p);
			if (key < min) min = key;
			if (key > max) max = key;
		}
		System.out.println("min: " + min + " max: " + max );
		}
	public void setRadius(float radius) { this.h = radius; this.hh = h*h;}
	public void setDensity(float density) {	this.rho0 = density; }
	public void setStiffness(float stiffness) {	this.k = stiffness; this.k_ = k*10f; }
	public void setSigma(float sigma) {	this.sigma = sigma;	}
	public void setBeta(float beta) { this.beta = beta;	}
	public void setAttractor(Vector2D p) { this.attractor = p; }
	
	// Source
	public Liquid(SpatialTable<Particle> table) {
		if (table == null) {
			throw new IllegalArgumentException("SpatialTable instance in null");
		}
		particles = table;
		attractor = new Vector2D(-1, -1);
	}
	
	private static float rand() {
		return new Random().nextFloat()*2-1;
	}
	
	public Dimension getSize() {
		return new Dimension(200, 400);
	}
	
	public void populate() {
		Dimension size = getSize();
		int step = 7, i = 0;
		int N = 1500;
		for (int x = 10; x < size.width; x += step) {
			for (int y = 10; y < size.height && N > 0; y += step, ++i) {
				Particle p = new Particle();
				p.p = new Vector2D(x, y);
				p.pp = new Vector2D(x+rand(), y+rand());
				p.f = new Vector2D(0, 0);
				particles.add(p);
				N--;
			}
		}
		
		// hahs
		int min = 10000000, max = -10000000;
		for (Particle p : particles) {
			int key = particles.generate(p);
			if (key < min) min = key;
			if (key > max) max = key;
		}
		
		System.out.println("min: " + min + " max: " + max );
		
		System.out.println("Populated: " + i + " particles");
	}
	
	private void wallCollision(Particle p) {
		Dimension size = getSize();
		size.width 	-= Particle.r;
		size.height -= Particle.r;

		if (p.p.x > size.width) {
			p.v.substract(new Vector2D((p.p.x-size.width)/4, 0));
		} else if (p.p.x < 0) {
			p.v.add(new Vector2D((-p.p.x)/4, 0));
		} else if (p.p.y > size.height) {
			p.v.substract(new Vector2D(0, (p.p.y-size.height)/4));
		} else if (p.p.y < 0) {
			p.v.add(new Vector2D(0, (-p.p.y)/4));
		}
	}
	
	public void attract(Particle p) {
		int rsqrd = 2500;
		float k = .01f;
		Dimension size = getSize();
		
		if (attractor.x < 0 || attractor.x > size.width)  return;
		if (attractor.y < 0 || attractor.y > size.height) return;
		
		if (attractor.minus(p.p).lengthSquared() < rsqrd) {
			p.v.add(attractor.minus(p.p).scale(k));	
		}
	}
	
	@Override
	public void move() {
		// apply gravity
		for (Particle p : particles) {
			p.v = p.p.minus(p.pp); // compute next velocity
			Vector2D g = new Vector2D(0, G); //gravitation
			p.v.add(g);
			p.v.add(p.f);
			p.p.add(p.v);
			
			p.f = new Vector2D(0, 0);
			wallCollision(p);
			
			attract(p);
		}
		
		// apply viscosity
		viscosity();
	
		// save previous position
		for (Particle p : particles) {
			p.pp = p.p.clone();
			p.p.add(p.v);
		}
		
		// double density relaxation
		density();
	}
	
	private void viscosity() {
		for (int i = 0; i < particles.size()-1; ++i) {
			Particle pi = particles.get(i);
			pi.rho = 0;
			pi.rho_ = 0;
			
			for (int j = i+1; j < particles.size(); ++j) {	
				Particle pj = particles.get(j);
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.lengthSquared(); 
				if (q < hh) {
					q = (float)Math.sqrt(q);	// q is length	
					rij = rij.devide(q);		// now rij is normalized
					q /= h;						// find q
					
					Vector2D vij = pi.v.minus(pj.v);
					float u = vij.dot(rij);
					
					float qq = (1-q)*(1-q);
					pi.rho += qq;
					pi.rho_+= qq*(1-q);
					
					if (u > 0) {
						float s = (1-q)*(sigma*u+beta*u*u);
						Vector2D I = rij.scale(s*.5f);
						pi.v.substract(I);
						pj.v.add(I);
						
						if (pi.v.x > 20 || pj.v.x > 20) {
							System.out.println("HAHA q: "  + " hh: " + hh + " ok: " + (q < 1) );
						}
					}
				}
			} // for
		}
	}
	
	private void density() {
		for (int i = 0; i < particles.size(); ++i) {
			Particle pi = particles.get(i);
			
			float P  = k*(pi.rho - rho0);
			float P_ = k_*pi.rho_;
			
			Vector2D dx = new Vector2D(0, 0);
			
			for (int j = i+1; j < particles.size(); ++j) {
				Particle pj = particles.get(j);
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.lengthSquared();
				if (q < hh) {
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