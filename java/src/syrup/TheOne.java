package syrup;

import java.awt.BorderLayout;
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
		SpatialTable<Particle> particles = new SpatialTable<Particle>() {

			@Override
			protected int generate(Particle value) {
				int p1 = 73856093;
				int p2 = 19349663;
				
				int i = (int)((value.p.x+.03f)/10);
				int j = (int)((value.p.y+.03f)/10);
				return ( (i*p1)^(j*p2) )%10000;
			}
		};
		
		liquid = new Liquid(particles);	
		canvas = new Canvas(particles);
		loop = new Loop(liquid, canvas);
		
		// Build GUI
		ControlPanel cp = new ControlPanel();
		cp.addControlsListener(this);
		canvas.setPreferredSize(liquid.getSize());
		MouseHandler mouseHandler = new MouseHandler();
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseHandler);
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
			liquid.setGravity(e.getValue());
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
		public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);
			liquid.setAttractor(new Vector2D(-1f, -1f));
		}
		
	}
	
	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		one.setVisible(true);
	}

}
