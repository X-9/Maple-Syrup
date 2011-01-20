package syrup;

import java.awt.GridLayout;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.Box;
import javax.swing.JComponent;
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
	
	protected final EventListenerList listenerList;
	

	public ControlPanel() {
		listenerList = new EventListenerList();
		
		initGui();
	}
	
	private void initGui() {
		Box box = Box.createVerticalBox();
		box.add(new FloatSlider(GRAVITY, .06f, .01f, .1f));
		box.add(new FloatSlider(RADIUS, 10f, 6f, 12f));
		box.add(new FloatSlider(DENSITY, 10f, .01f, 20f));
		box.add(new FloatSlider(STIFFNESS, .004f, .001f, .01f));
		box.add(new FloatSlider(SIGMA, .0f, .0f, 1f));
		box.add(new FloatSlider(BETA, .3f, .1f, .4f));
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
	
	private class FloatSlider extends JComponent {
		private static final long serialVersionUID = 1L;

		public FloatSlider(final String name, final float value, final float min, final float max) {
			if (min >= max || value < min || value > max) {
				throw new IllegalArgumentException("invalid range properties");
			}
			final float tick = (max-min)/100;
			JSlider slider = new JSlider(0, 100);
			slider.setValue((int)(value/tick));
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					float value = ((JSlider)e.getSource()).getValue()*tick+min;
					fireEvent(new ControlsEvent(this, name, value));
				}
			});
			
			setLayout(new GridLayout(3, 1));
			add(new JLabel(name));
			add(slider);
			add(Box.createVerticalGlue());
		}		
	}
}
