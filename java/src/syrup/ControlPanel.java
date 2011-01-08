package syrup;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

class ControlsEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public ControlsEvent(Object source) {
		super(source);
	}
}

interface ControlsListener extends EventListener {
	abstract public void controlsPerformed(ControlsEvent e);
}

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected final EventListenerList listenerList;

	public ControlPanel() {
		listenerList = new EventListenerList();
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
