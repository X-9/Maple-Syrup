package syrup;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class TheOne extends JFrame implements ControlsListener {
	private static final long serialVersionUID = 1L;

	private Liquid liquid;	// Main engine
	private Canvas canvas;	// Draws elements on the screen
	private Loop loop;		// What could it be?
	
	public TheOne() {
		// Collection contains all liquid elements
		SpatialTable<Particle> particles = new SpatialTable<Particle>();
		
		liquid = new Liquid(particles);	
		canvas = new Canvas(particles);
		loop = new Loop(liquid, canvas);
		
		// Build GUI
		ControlPanel cp = new ControlPanel();
		cp.addControlsListener(this);
		canvas.setPreferredSize(liquid.getSize());
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
		
		if (ControlPanel.STIFFNESS_NEAR.equals(e.getName())) {
			liquid.setYetAnotherParamener(e.getValue());
		}
		
		if (ControlPanel.SIGMA.equals(e.getName())) {
			liquid.setSigma(e.getValue());
		}
		
		if (ControlPanel.BETA.equals(e.getName())) {
			liquid.setBeta(e.getValue());
		}
		
		
		
		
	}
	
	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		one.setVisible(true);
	}

}
