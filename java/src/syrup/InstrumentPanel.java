package syrup;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class InstrumentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public static final String MAGNET 	= "Magnet";
	public static final String ROTATOR 	= "Rotator";
	public static final String EMITTER 	= "Emmiter";
	
	private ButtonGroup group;
	
	public InstrumentPanel() {
		group = new ButtonGroup();
		JToggleButton magnet = makeButton(MAGNET, "img/magnet.png", "img/magnet_pressed.png");
		JToggleButton rotator = makeButton(ROTATOR, "img/rotate.png", "img/rotate_pressed.png");
		JToggleButton emitter = makeButton(EMITTER, "img/liquid.png", "img/liquid_pressed.png");
		emitter.setSelected(true);
		
		group.add(emitter);
		group.add(rotator);
		group.add(magnet);
			
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(emitter);
		add(rotator);
		add(magnet);
	}
	
	private JToggleButton makeButton(String actionCommand, String img, String pressedImg) {

		JToggleButton button = new JToggleButton(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(img))));
		button.setPressedIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(pressedImg))));
		button.setSelectedIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(pressedImg))));
		button.setActionCommand(actionCommand);
		button.setFocusable(false);
		button.setContentAreaFilled(false);
		return button;
	}
	
	public void addActionListener(ActionListener l) {
		for (Enumeration<AbstractButton> e = group.getElements(); 
			e.hasMoreElements(); 
			e.nextElement().addActionListener(l))
			;
	}
	
	public void removeActionListener(ActionListener l) {
		for (Enumeration<AbstractButton> e = group.getElements(); 
			e.hasMoreElements(); 
			e.nextElement().removeActionListener(l))
			;
	}

}
