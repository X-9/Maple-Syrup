package syrup;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TheOne extends JFrame implements ControlsListener, ActionListener {
	private static final long serialVersionUID = 1L;

	// Collection contains all liquid elements
	private final static SpatialTable<Particle> particles = new SpatialTable<Particle>(200, 300) {
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
	
	private final static Liquid liquid = new Liquid(particles);	// Main engine
	private final static Canvas canvas = new Canvas(particles);	// Draws elements on the screen
	private final static Loop loop = new Loop(liquid, canvas);	// What could it be?
	
	private MouseOptions mouseOptions;							// Set of available mouse options
	
	private enum MouseOptions {
		EMITTER() {
			@Override
			public void pressed(MouseEvent e) {
				Point p = canvas.projection(e.getPoint());
				liquid.beginEmit(p.x, p.y);
			}

			@Override
			public void dragged(MouseEvent e) {
				pressed(e);
			}

			@Override
			public void released(MouseEvent e) {
				liquid.endEmit();
			}
			
		},
		MAGNET() {
			@Override
			public void pressed(MouseEvent e) {
				Point p = canvas.projection(e.getPoint());
				liquid.setAttractor(new Vector2D(p.x, p.y));
			}

			@Override
			public void dragged(MouseEvent e) {
				pressed(e);
			}

			@Override
			public void released(MouseEvent e) {
				liquid.setAttractor(new Vector2D(-1f, -1f));
			}
			
		},		
		ROTATE() {
			private double start = 0;
			
			@Override
			public void pressed(MouseEvent e) {
				start = getRadianAngle(new Point(200, 200), e.getPoint());
			}

			@Override
			public void dragged(MouseEvent e) {
				// calculate relative rotation angle
				Point centre = new Point(canvas.getWidth()/2, canvas.getHeight()/2);
				double finish = getRadianAngle(centre, e.getPoint());
				
				canvas.addRotationAngle(finish-start);	// add relative angle
				
				// however new gravity could be found with absolute rotation angle
				liquid.turnGravity((float)canvas.getAbsoluteAngle());
				
				start = finish;
			}

			@Override
			public void released(MouseEvent e) {
				
			}
			
			private double getRadianAngle(Point c, Point p) {
				double dx = c.getX() - p.getX();
				double dy = c.getY() - p.getY();
				
				return Math.atan2(dy, dx);
			}
		};
		
		public abstract void pressed(MouseEvent e);
		public abstract void dragged(MouseEvent e);
		public abstract void released(MouseEvent e);
	};
	
	public TheOne() {
		// Build GUI
		ControlPanel cp = new ControlPanel();
		cp.addControlsListener(this);
		canvas.setPreferredSize(liquid.getSize());
		
		InstrumentPanel np = new InstrumentPanel();
		np.addActionListener(this);

		MouseTool mouseTool = new MouseTool();
		canvas.addMouseListener(mouseTool);
		canvas.addMouseMotionListener(mouseTool);
		
		add(canvas, BorderLayout.CENTER);
		
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(cp, BorderLayout.CENTER);
		eastPanel.add(np, BorderLayout.NORTH);
		add(eastPanel, BorderLayout.EAST);
		pack();
		
		mouseOptions = MouseOptions.EMITTER;
		
		// Start The Ignition
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
			liquid.turnGravity((float)canvas.getAbsoluteAngle());
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (InstrumentPanel.MAGNET.equals(e.getActionCommand())) {
			mouseOptions = MouseOptions.MAGNET;
		}
		
		if (InstrumentPanel.ROTATOR.equals(e.getActionCommand())) {
			mouseOptions = MouseOptions.ROTATE;
		}
		
		if (InstrumentPanel.EMITTER.equals(e.getActionCommand())) {
			mouseOptions = MouseOptions.EMITTER;
		}
	}
	
	private class MouseTool extends MouseAdapter {

		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			mouseOptions.dragged(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			mouseOptions.pressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			mouseOptions.released(e);
		}
		
	}
	
	
	public static void main(String[] args) {
		TheOne one = new TheOne();
		one.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		one.setVisible(true);
	}
}
