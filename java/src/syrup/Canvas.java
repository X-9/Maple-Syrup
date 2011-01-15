package syrup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class Canvas extends JComponent implements Render {
	private static final long serialVersionUID = 1L;
	
	private final Iterable<Particle> elements;
	private float theta;
	private long diff = 0;
	
	
	public Canvas(final Iterable<Particle> elements) {
		if (elements == null) {
			throw new IllegalArgumentException
			("Failed to initialize canvas, elements collection is empty.");
		}
		
		this.elements = elements;

		initBorders();
		
		theta = 0f;
		
		setIgnoreRepaint(true);
	}
	
	private void initBorders() {

	}
	
	public void setRotationAngle(float angle) {
		theta = angle;
	}
	
	@Override
	public void display() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {		
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform transformer = new AffineTransform();
		int anchorx = 200/2;
		int anchory = 300/2;
		transformer.rotate(theta, anchorx, anchory);
		g2.setTransform(transformer);
	
		final float c = Particle.r/2;	// find centre of particle
		final float s = Particle.r;		// particle size
			
		for (Particle p : elements) {
			Ellipse2D e = new Ellipse2D.Float(p.p.x-c, p.p.y-c, s, s);
			g2.setColor(new Color(0, 255-(int)(p.rho*15%255), 254));
			g2.fill(e);
		}
		
		// calculate fps :)
		g2.drawString(String.valueOf(System.currentTimeMillis()-diff), 20, 20);
		diff = System.currentTimeMillis();
		
		g2.dispose();
	}
	
}
