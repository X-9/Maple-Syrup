package syrup;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class TheOne extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Liquid liquid = new Liquid();
		one.add(liquid, BorderLayout.CENTER);
		one.pack();
		
		liquid.populate();
		new Loop(liquid, liquid);
		
		one.setVisible(true);
	}

}
