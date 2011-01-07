package syrup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.JComponent;

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
public class Liquid extends JComponent implements Idle, Render {
	private static final long serialVersionUID = 1L;

	static private final float G = .06f;	// gravity
	static private final float h = 10.f;		// interaction radius
	static private final float rho0 = 10f;	// rest density
	static private final float k = .004f;		// stiffness
	static private final float k_ = .01f;	// yet another parameter
	static private final float sigma = 0f;	// sigma
	static private final float beta = h/10;	// beta
	static private final float r = 1.5f;	//

	
	private SpatialTable<Particle> particles;
	
	public Liquid() {
		particles = new SpatialTable<Particle>();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}
	
	private static float rand() {
		return new Random().nextFloat()*2-1;
	}
	
	public void populate() {
		Dimension size = getSize();
		int step = 10, i = 0;
		for (int n = step; n < size.width*size.height; n += step, i++) {
			Particle p = new Particle();
			float x = n%size.width;
			float y = n%size.height;
			
			p.p = new Vector2D(x, y);
			p.v = new Vector2D(rand(), rand());
			
			particles.add(p);
		}
		
		System.out.println("Populated: " + i + " particles");
	}
	
	private void wallCollision(Particle p) {
		Dimension size = getSize();
		size.width -= r;
		size.height -= r;

		if (p.p.x > size.width) {
			p.v.substract(new Vector2D((p.p.x-size.width)/2, 0));
		}
		
		if (p.p.x < 0) {
			p.v.add(new Vector2D((0-p.p.x)/2, 0));
		}
		
		if (p.p.y > size.height) {
			p.v.substract(new Vector2D(0, (p.p.y-size.height)/2));
		}
		
		if (p.p.y < 0) {
			p.v.add(new Vector2D(0, (0-p.p.y)/2));
		}
	}
	
	@Override
	public void move() {
		// apply gravity
		for (Particle p : particles) {
			Vector2D g = new Vector2D(0, G); //gravitation
			p.v.add(g);
			p.p.add(p.v);
			
			wallCollision(p);
		}
		
		// apply viscosity
		viscosity();
		
		for (Particle p : particles) {
			p.pp = p.p.clone();
			p.p.add(p.v);
		}
		
		// double density relaxation
		density();
		
		for (Particle p : particles) {
			p.v = p.p.minus(p.pp);
		}
	}
	
	private void viscosity() {
		for (int i = 0; i < particles.size()-1; ++i) {
			Particle pi = particles.get(i);
			
			for (int j = i+1; j < particles.size(); ++j) {	
				Particle pj = particles.get(j);
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.length()/h;
				if (q < 1) {
					rij.normalize();
					Vector2D vij = pi.v.minus(pj.v);
					float u = vij.dot(rij);
					
					if (u > 0) {
						float s = (1-q)*(sigma*u+beta*u*u);
						Vector2D I = rij.scale(s);
						pi.v.substract(I.scale(0.5f));
						pj.v.add(I.scale(0.5f));
					}
				}
			} // for
		}
	}
	
	private void density() {
		for (int i = 0; i < particles.size()-1; ++i) {
			Particle pi = particles.get(i);
			
			float rho = 0;
			float rho_ = 0;
			
			for (int j = i+1; j < particles.size(); ++j) {	
				Particle pj = particles.get(j);
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.length()/h;
				if (q < 1) {
					rho += (1-q)*(1-q);
					rho_+= (1-q)*(1-q)*(1-q);
				}
			} // for
			
			float P  = k*(rho - rho0);
			float P_ = k_*rho_;
			
			Vector2D dx = new Vector2D(0, 0);
			
			for (int j = i+1; j < particles.size(); ++j) {
				Particle pj = particles.get(j);
				
				Vector2D rij = pj.p.minus(pi.p);
				float q = rij.length()/h;
				if (q < 1) {
					float a = P*(1-q)+P_*(1-q)*(1-q);
					
					Vector2D uij = rij.getNormalized().scale(a);
					pj.p.add(uij.scale(0.5f));
					dx.substract(uij.scale(0.5f));
				}
			} // for
			pi.p.add(dx);
		}
	}
	
	@Override
	public void display() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final float s = 2*r;
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.cyan);
		for (Particle p : particles) {
			Ellipse2D e = new Ellipse2D.Float(p.p.x-r, p.p.y-r, s, s);
			g2.draw(e);
		}
		
		g2.dispose();
	}
	
	
}
