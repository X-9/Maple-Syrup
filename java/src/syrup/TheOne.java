package syrup;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class TheOne extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Collection contains all liquid elements
		SpatialTable<Particle> particles = new SpatialTable<Particle>();
		
		Liquid liquid = new Liquid(particles);		// Main engine
		Canvas canvas = new Canvas(particles);		// Draws elements on the screen
		Loop loop = new Loop(liquid, canvas);		// What could it be?
		
		// Build GUI
		canvas.setPreferredSize(liquid.getSize());
		one.add(canvas, BorderLayout.CENTER);
		one.pack();
		
		// Start The Ignition
		liquid.populate();
		loop.start();
		
		one.setVisible(true);
	}

}
