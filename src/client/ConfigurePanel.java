package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ConfigurePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Font menuFont, editorFont;
	private ImageIcon buttonBackground, rolloverBackground;
	private JButton wordlistButton, keyboardButton, configureCloseButton;
	private JLabel wordlistLabel, keyboardLabel;
	private JPanel configureContentsPanel;
	private MacroHardClient mHO;
	
	public ConfigurePanel(MacroHardClient mHO, Font menuFont, Font editorFont, ImageIcon buttonBackground,
			ImageIcon rolloverBackground) {
		this.mHO = mHO;
		this.menuFont = menuFont;
		this.editorFont = editorFont;
		this.buttonBackground = buttonBackground;
		this.rolloverBackground = rolloverBackground;
		setLayout(new BorderLayout());
		initializeComponents();
		createGUI();
		addEvents();
	}

	private void initializeComponents() {
		configureContentsPanel = new JPanel();
		wordlistLabel = new JLabel(mHO.wordlist.getName());
		keyboardLabel = new JLabel(mHO.keyboard.getName());
		wordlistButton = new JButton("Select Word List File", buttonBackground);
		keyboardButton = new JButton("Select Keyboard File", buttonBackground);
		configureCloseButton = new JButton("Close", buttonBackground);
	}
	
	private void createGUI() {
		//Style buttons
		JButton[] buttons = {wordlistButton, keyboardButton, configureCloseButton};
		Insets none = new Insets(0, 0, 0, 0);
		EmptyBorder noBorder = new EmptyBorder(0, 0, 0, 0);
		for(JButton b : buttons) {
			b.setFont(editorFont);
			b.setSize(150, 25);
			b.setRolloverIcon(rolloverBackground);
			b.setForeground(Color.GRAY);
			b.setHorizontalTextPosition(SwingConstants.CENTER);
			b.setMargin(none);
			b.setContentAreaFilled(false);
			b.setBorder(noBorder);
		}
		
		//Style configure items
		wordlistLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
		keyboardLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
		wordlistLabel.setFont(editorFont);
		keyboardLabel.setFont(editorFont);
		wordlistLabel.setForeground(Color.WHITE);
		keyboardLabel.setForeground(Color.WHITE);
		
		configureContentsPanel.setLayout(new BoxLayout(configureContentsPanel, BoxLayout.Y_AXIS));
		configureContentsPanel.add(wordlistLabel);
		configureContentsPanel.add(wordlistButton);
		configureContentsPanel.add(keyboardLabel);
		configureContentsPanel.add(keyboardButton);
		configureContentsPanel.setBorder(new TitledBorder(new LineBorder(Color.WHITE), "Configure",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, menuFont, Color.WHITE));
		configureContentsPanel.setBackground(Color.GRAY);
		add(configureContentsPanel);
		add(configureCloseButton, BorderLayout.PAGE_END);
	}
	
	private void addEvents() {
		//Calibrate panel wordlist button
		wordlistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".wl", "wl", "wordlist");
				mHO.wordlist = selectWLKB("Select Wordlist File...", filter, mHO.wordlist);
				wordlistLabel.setText(mHO.wordlist.getName());
			}
		});
		//Calibrate panel keyboard button
		keyboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".kb", "kb", "keyboard");
				mHO.keyboard = selectWLKB("Select Keyboard File...", filter, mHO.keyboard);
				keyboardLabel.setText(mHO.keyboard.getName());
			}
		});
		//Calibrate panel close button
		configureCloseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mHO.removeConfigPanel();
			}
		});
	}
	
	//Allows the user to select another wordlist or keyboard file, returns the file the user selects
	private File selectWLKB(String message, FileNameExtensionFilter filter, File file) {
		JFileChooser fC = new JFileChooser();
		fC.setDialogTitle(message);
		fC.setAcceptAllFileFilterUsed(false);
		fC.setFileFilter(filter);
		fC.setCurrentDirectory(file);
		fC.setSelectedFile(file);
		int result = fC.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fC.getSelectedFile();
		    return selectedFile;
		}
		return file;
	}
}
