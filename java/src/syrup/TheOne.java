package syrup;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class TheOne extends JFrame implements ControlsListener {
	private static final long serialVersionUID = 1L;

	private Liquid liquid;	// Main engine
	private Canvas canvas;	// Draws elements on the screen
	private Loop loop;		// What could it be?
	
	public TheOne() {
		// Collection contains all liquid elements
		final SpatialTable<Particle> particles = new SpatialTable<Particle>(200, 300) {
			private final static int CELL_SIZE = 10;
			
			@Override
			protected int posX(Particle value) {
				return (int)((value.p.x+.3f)/CELL_SIZE);
			}

			@Override
			protected int posY(Particle value) {
				return (int)((value.p.y+.3f)/CELL_SIZE);
			}
		};
		
		liquid = new Liquid(particles);	
		canvas = new Canvas(particles);
		loop = new Loop(liquid, canvas);
		
		// Build GUI
		ControlPanel cp = new ControlPanel();
		cp.addControlsListener(this);
		canvas.setPreferredSize(liquid.getSize());
		MouseRotate mr = new MouseRotate();
		canvas.addMouseListener(mr);
		canvas.addMouseMotionListener(mr);
		//MouseHandler mouseHandler = new MouseHandler();
		//canvas.addMouseListener(mouseHandler);
		//canvas.addMouseMotionListener(mouseHandler);
		add(canvas, BorderLayout.CENTER);
		add(cp, BorderLayout.EAST);
		pack();
		
		// Start The Ignition
		liquid.populate();
		loop.start();
	}
	
	@Override
	public void controlsPerformed(ControlsEvent e) {
		if (ControlPanel.RADIUS.equals(e.getName())) {
			liquid.setRadius(e.getValue());
		}
		
		if (ControlPanel.DENSITY.equals(e.getName())) {
			liquid.setDensity(e.getValue());
		}
		
		if (ControlPanel.GRAVITY.equals(e.getName())) {
			liquid.setGravityY(e.getValue());
		}
		
		if (ControlPanel.STIFFNESS.equals(e.getName())) {
			liquid.setStiffness(e.getValue());
		}
		
		if (ControlPanel.SIGMA.equals(e.getName())) {
			liquid.setSigma(e.getValue());
		}
		
		if (ControlPanel.BETA.equals(e.getName())) {
			liquid.setBeta(e.getValue());
		}
		
		/*
		if (ControlPanel.ROTATION.equals(e.getName())) {
			canvas.setRotationAngle(e.getValue());
			float x = (float) (.06f*Math.sin(e.getValue()));
			float y = (float) (.06f*Math.cos(e.getValue()));
			liquid.setGravityX(x);
			liquid.setGravityY(y);
		}
		*/

	}
	
	private class MouseHandler extends MouseAdapter {
		
		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			mousePressed(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			liquid.setAttractor(new Vector2D(e.getPoint().x, e.getPoint().y));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			liquid.setAttractor(new Vector2D(-1f, -1f));
		}
		
	}
	
	private class MouseRotate extends MouseAdapter {
		private double start;

		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			
			// calculate relative rotation angle
			Point centre = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
			double finish = getRadianAngle(centre, e.getPoint());
			
			canvas.addRotationAngle(finish-start);	// add relative angle
			
			// however new gravity could be found with absolute rotation angle
			float gx = (float) (.06f*Math.sin(canvas.getAbsoluteAngle()));
			float gy = (float) (.06f*Math.cos(canvas.getAbsoluteAngle()));
			
			// set new gravity forces to liquid
			liquid.setGravityX(gx);
			liquid.setGravityY(gy);
			
			start = finish;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
		
			start = getRadianAngle(new Point(200, 200), e.getPoint());
		}
		
		private double getRadianAngle(Point c, Point p) {
			double dx = c.getX() - p.getX();
			double dy = c.getY() - p.getY();
			
			return Math.atan2(dy, dx);
		}
	}

	
	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		one.setVisible(true);
	}

}
