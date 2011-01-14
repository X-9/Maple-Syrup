package syrup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.swing.JComponent;

public class Canvas extends JComponent implements Render {
	private static final long serialVersionUID = 1L;
	
	public enum Type { WATER, VELOCITY, POSITION };
	private Type type;
	
	private final Collection<Particle> elements;
	private long diff = 0;
	
	public Canvas(final Collection<Particle> elements) {
		if (elements == null) {
			throw new IllegalArgumentException
			("Failed to initialize canvas, elements collection is empty.");
		}
		
		this.elements = elements;
		type = Type.WATER;
		setIgnoreRepaint(true);
	}
	
	public void setDisplayType(Type newType) {
		type = newType;
	}
	
	@Override
	public void display() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
	
		
		final float r = Particle.r/2;
		switch (type) {
		case WATER:
			final float s = Particle.r;
			
			for (Particle p : elements) {
				Ellipse2D e = new Ellipse2D.Float(p.p.x-r, p.p.y-r, s, s);
				Color m = new Color(0, (int)(p.rho*10%255), 255);
				g2.setColor(m);
				g2.fill(e);
			}	
			break;
		}	
		
		g2.drawString(String.valueOf(System.currentTimeMillis()-diff), 20, 20);
		diff = System.currentTimeMillis();
		g2.dispose();
		
	}
	
}
