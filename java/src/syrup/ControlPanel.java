package syrup;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


class ControlsEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private final float value;
	private final String name;

	public ControlsEvent(Object source, String name, float value) {
		super(source);
		this.name = name; this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public float getValue() {
		return value;
	}
}

interface ControlsListener extends EventListener {
	abstract public void controlsPerformed(ControlsEvent e);
}

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final String GRAVITY 			= "Gravity";
	public static final String RADIUS 			= "Interaction Radius";
	public static final String DENSITY 			= "Rest Density";
	public static final String STIFFNESS 		= "Stiffness";
	public static final String STIFFNESS_NEAR 	= "Stiffness Near";
	public static final String SIGMA 			= "Sigma";
	public static final String BETA 			= "Beta";
	
	protected final EventListenerList listenerList;
	

	public ControlPanel() {
		listenerList = new EventListenerList();
		
		initGui();
	}
	
	private Box make(final String name, Float[] limits) {
		if (limits.length != 4) {
			throw new IllegalArgumentException();
		}
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(limits[0], limits[1], limits[2], limits[3]));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				float value = (Float)((JSpinner)e.getSource()).getValue();
				fireEvent(new ControlsEvent(this, name, (float)value));	
			}
		});
		Box box = Box.createHorizontalBox();
		box.add(new JLabel(name));
		box.add(Box.createHorizontalGlue());
		box.add(spinner);
	
		return box;
	}

	private void initGui() {
		Box box = Box.createVerticalBox();
		box.add(make(GRAVITY, new Float[] {.06f, .01f, 5f, .01f}));
		box.add(make(RADIUS, new Float[] {10f, 1f, 20f, 1f}));
		box.add(make(DENSITY, new Float[] {10f, 1f, 20f, 1f}));
		box.add(make(STIFFNESS, new Float[] {.004f, .001f, 1f, .005f}));
		box.add(make(STIFFNESS_NEAR, new Float[] {.15f, .001f, 1f, .005f}));
		box.add(make(SIGMA, new Float[] {0f, 0f, 10f, 1f}));
		box.add(make(BETA, new Float[] {.5f, .1f, 1f, .1f}));
		add(box);
	}
	
	
	public void addControlsListener(ControlsListener listener) {
		listenerList.add(ControlsListener.class, listener);
	}
	
	public void removeControlsListener(ControlsListener listener) {
		listenerList.remove(ControlsListener.class, listener);
	}
	
	private void fireEvent(ControlsEvent e) {
		for (ControlsListener listener : listenerList.getListeners(ControlsListener.class)) {
			listener.controlsPerformed(e);
		}
	}
	
}
