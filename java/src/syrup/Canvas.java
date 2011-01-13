package syrup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		RenderingHints rh = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		
		final float r = Particle.r/2;
		
		switch (type) {
		case WATER:
			final float s = Particle.r;
			
			for (Particle p : elements) {
				float c = 1-p.rho*0.05f;
				c = (c < 0) ? 0 : c;
				Ellipse2D e = new Ellipse2D.Float(p.p.x-r, p.p.y-r, s, s);
				g2.setColor(new Color(0, c, 1));
				g2.fill(e);
			}	
			break;
		
		case POSITION:
			for (Particle p : elements) {
				Line2D l = new Line2D.Float(p.p.x-r, p.p.y-r, p.pp.x-r, p.pp.y-r);
				g2.setColor(Color.black);
				g2.draw(l);
			}	
			break;
		
		case VELOCITY:
			for (Particle p : elements) {
				Line2D l = new Line2D.Float(p.p.x-r, p.p.y-r, p.p.x+p.v.x, p.p.y+p.v.y);
				g2.setColor(Color.black);
				g2.draw(l);
			}	
			break;
		}	
		
		g2.drawString(String.valueOf(System.currentTimeMillis()-diff), 20, 20);
		diff = System.currentTimeMillis();
		g2.dispose();
	}
	
}
