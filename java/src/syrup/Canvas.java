package syrup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class Canvas extends JComponent implements Render {
	private static final long serialVersionUID = 1L;
	
	private final Iterable<Particle> elements;	// collection of elements to draw
	private final BufferedImage background;
	private final BufferedImage foreground;
	private final Graphics2D canvas;			// draw elements on this canvas
	private final AffineTransform transformer;	// use it to rotate layouts
	private final Point zero;					//
	private Dimension lsize;					// layouts size
	private double theta;						// absolute rotation angle in radians
	private long diff = 0;						// fps
	
	
	public Canvas(final Iterable<Particle> elements) {
		if (elements == null) {
			throw new IllegalArgumentException
			("Failed to initialize canvas, elements collection is empty.");
		}
		
		this.elements = elements;
		
		lsize = new Dimension(200, 300);
		zero = new Point(100, 50);
		
		// obviously two layouts
		background = new BufferedImage(lsize.width, lsize.height, BufferedImage.TYPE_INT_RGB);
		foreground = new BufferedImage(lsize.width, lsize.height, BufferedImage.TYPE_INT_ARGB);
		
		canvas = (Graphics2D)foreground.getGraphics();	// make paint work on foreground
		
		transformer = canvas.getTransform();			// transformation object
		theta = 0;										// initial rotation angle
		
		// draw background
		initBackground();
		
		// repaint canvas manually only
		setIgnoreRepaint(true);
	}
	
	private void initBackground() {
		Graphics2D g2 = (Graphics2D)background.getGraphics();
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g2.setRenderingHints(rh);
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, lsize.width, lsize.height);
		
		g2.setColor(Color.BLACK);
		g2.drawRect(1, 1, lsize.width-2, lsize.height-2);
		g2.drawLine(lsize.width/2-10, lsize.height/2, lsize.width/2+10, lsize.height/2);
		g2.drawLine(lsize.width/2, lsize.height/2-10, lsize.width/2, lsize.height/2+10);
		
	}
	
	public void addRotationAngle(double d) {
		// rotate to relative angle
		transformer.rotate(d, getWidth()/2, getHeight()/2);
		
		// add difference to absolute angle, take mod to avoid overflow
		theta = (theta+d)%(2*Math.PI);
	}
	
	public Point projection(Point p) {
		Point result = (Point)p.clone();
		try {
			transformer.createInverse().transform(p, result);
		} catch (NoninvertibleTransformException e) {
			return result;
		}
		
		result.x -= zero.x;
		result.y -= zero.y;
		return result;
	}
	
	public Point translate(Point p) {
		Point result = (Point)p.clone();
		transformer.transform(p, result);
		return result;
	}
	
	public double getAbsoluteAngle() {
		return theta;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
	
	@Override
	/**
	 * Use passive rendering, because Java repaints component in separate thread.
	 */
	public void display() {
		// clear screen, filling it with default background colour
		canvas.setBackground(new Color(0, 0, 0, 0));
		canvas.clearRect(0, 0, getWidth(), getHeight());
		
		final float c = Particle.r/2;	// find centre of particle
		final float s = Particle.r;		// particle size
		
		// draw all particles on the canvas
		for (Particle p : elements) {
			Ellipse2D e = new Ellipse2D.Float(p.p.x-c, p.p.y-c, s, s);
			canvas.setColor(new Color(0, 255-(int)(p.rho*15%255), 254));
			canvas.fill(e);
		}
		
		Graphics2D g2 = (Graphics2D)getGraphics();
		
		g2.clearRect(0, 0, 400, 400);
		
		// rotate image
		g2.setTransform(transformer);
		
		g2.drawImage(background, zero.x, zero.y, this);	// draw buffered background
		g2.drawImage(foreground, zero.x, zero.y, this);	// and foreground
		
		// calculate fps :)
		g2.drawString(String.valueOf(System.currentTimeMillis()-diff), 20, 20);
		diff = System.currentTimeMillis();
		
		g2.dispose();
	}
}
