package syrup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;


/**
 * Let's rock!!!
 */
public class Liquid extends JComponent implements Idle, Render {
	private static final long serialVersionUID = 1L;

	static private final float G = .06f; // gravity
	
	private SpatialTable<Particle> particles;
	
	public Liquid() {
		particles = new SpatialTable<Particle>();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}
	
	public void populate() {
		Dimension size = getSize();
		int step = 8;
		for (int n = step; n < size.width*size.height; n += step) {
			Particle p = new Particle();
			float x = n%size.width;
			float y = n%size.height;
			
			p.p = new Vector2D(x, y);
			p.v = new Vector2D(0, 0);
			
			particles.add(p);
		}
	}
	
	@Override
	public void move() {
		// apply gravity
		for (Particle p : particles) {
			Vector2D g = new Vector2D(0, G);
			p.v.plus(g);
			p.p.plus(p.v);
		}
		
		// apply viscosity
		viscosity();
		
		// double density relaxation
		density();
	}
	
	private void viscosity() {
		
	}
	
	private void density() {
		
	}
	
	@Override
	public void display() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final float r = 1.5f;
		final float s = 2*r;
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.cyan);
		for (Particle p : particles) {
			Ellipse2D e = new Ellipse2D.Float(p.p.x-r, p.p.y-r, s, s);
			g2.fill(e);
		}
		
	}
	
	
}
