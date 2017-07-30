package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MacroHardServerGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private boolean started;
	private ImageIcon background, rollover;
	private JButton startStop;
	private JScrollPane scroll;
	private JTextArea log;
	private MacroHardServer mHS;
	
	public MacroHardServerGUI(MacroHardServer mHS) {
		super("MacroHardOffice Server");
		this.mHS = mHS;
		setSize(800, 600);
		background = new ImageIcon("resources/img/backgrounds/lightbutton.gif");
		background.setImage(background.getImage().getScaledInstance(getWidth(), 30, Image.SCALE_SMOOTH));
		rollover = new ImageIcon("resources/img/backgrounds/darkbutton.gif");
		rollover.setImage(rollover.getImage().getScaledInstance(getWidth(), 30, Image.SCALE_SMOOTH));
		started = false;
		initializeComponents();
		createGUI();
		addEvents();
		setVisible(true);
	}
	
	private void initializeComponents() {
		startStop = new JButton("Start", background);
		log = new JTextArea();
		scroll = new JScrollPane(log);
	}
	
	private void createGUI() {
		log.setSelectionColor(Color.GRAY);
		log.setEditable(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBackground(Color.GRAY);
		scroll.getVerticalScrollBar().setUI((new BasicScrollBarUI() {   
	        @Override
	        protected JButton createDecreaseButton(int orientation) {
	        	return makeScrollButton("up");
	        }

	        @Override    
	        protected JButton createIncreaseButton(int orientation) {
	        	return makeScrollButton("down");
	        }
	        @Override 
	        protected void configureScrollBarColors(){
	        	thumbColor = Color.GRAY;
	        	trackColor = Color.WHITE;
	        }
	    }));
		add(scroll, BorderLayout.CENTER);
		startStop.setRolloverIcon(rollover);
		startStop.setForeground(Color.GRAY);
		startStop.setHorizontalTextPosition(SwingConstants.CENTER);
		startStop.setMargin(new Insets(0, 0, 0, 0));
		startStop.setContentAreaFilled(false);
		startStop.setBorder(new EmptyBorder(0, 0, 0, 0));
		startStop.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(startStop, BorderLayout.SOUTH);
	}
	
	private void addEvents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(!started) {
					if(mHS.start()) startStop.setText("Stop");
					else started = !started;
				} else {
					startStop.setText("Start");
					mHS.stop();
				}
				started = !started;
			}
		});
	}
	
	private JButton makeScrollButton(String orientation) {
		JButton button = new JButton();
    	button.setSize(15, 15);
    	button.setBackground(Color.WHITE);
    	button.setBorder(null);
    	ImageIcon iI = new ImageIcon("resources/img/scrollbar/" + orientation + ".png");
    	iI.setImage(iI.getImage().getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_SMOOTH));
    	button.setIcon(iI);
    	return button;
	}
	
	public void appendToLog(String message) {
		log.append(message + "\n");
	}
}
