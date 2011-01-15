package syrup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Collection;

import javax.swing.JComponent;

public class Canvas extends JComponent implements Render {
	private static final long serialVersionUID = 1L;
	
	private final Collection<Particle> elements;
	private long diff = 0;
	
	public Canvas(final Collection<Particle> elements) {
		if (elements == null) {
			throw new IllegalArgumentException
			("Failed to initialize canvas, elements collection is empty.");
		}
		
		this.elements = elements;
		setIgnoreRepaint(true);
	}
	
	@Override
	public void display() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	
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
