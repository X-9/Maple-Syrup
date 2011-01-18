package syrup;

import java.awt.GridLayout;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
	public static final String DENSITY 			= "Density";
	public static final String STIFFNESS 		= "Stiffness";
	public static final String STIFFNESS_NEAR 	= "Stiffness Near";
	public static final String SIGMA 			= "Sigma";
	public static final String BETA 			= "Beta";
	public static final String ROTATION			= "Rotation";
	
	protected final EventListenerList listenerList;
	

	public ControlPanel() {
		listenerList = new EventListenerList();
		
		initGui();
	}
	
	private JSlider makeSlider(final String name, float init, float min, float max, final float scale) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL, (int)(min*scale), (int)(max*scale), (int)(init*scale));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				float value = ((JSlider)e.getSource()).getValue()/scale;
				fireEvent(new ControlsEvent(this, name, (float)value));	
			}
		});
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		labels.put(new Integer((int)((max*scale+min*scale)/2)), new JLabel(name));
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		
		return slider;
	}

	private void initGui() {
		setLayout(new GridLayout(0, 1));
		add(makeSlider(GRAVITY, .06f, .01f, .1f, 100));
		add(makeSlider(RADIUS, 11f, 6f, 12f, 1));
		add(makeSlider(DENSITY, 10f, .01f, 20f, 10));
		add(makeSlider(STIFFNESS, .004f, .001f, .01f, 1000));
		add(makeSlider(SIGMA, .0f, .0f, 1f, 100));
		add(makeSlider(BETA, .3f, .1f, .4f, 100));
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
