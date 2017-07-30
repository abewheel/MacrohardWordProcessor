package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import resource.JTextFieldLimit;
import resource.UserComm.UType;

public class MainPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private Client client;
	private Font nameFont, menuFont;
	private ImageIcon buttonBackground, rolloverBackground;
	private JButton optionsLoginButton, signUpButton, offlineButton, loginLoginButton, signUpLoginButton, backButton;
	private JLabel name, username, password, repeat;
	private JPanel optionsPanel, loginPanel, signUpPanel, optionsUtilPanel, usernamePanel, passwordPanel;
	private JPanel repeatPanel;
	private JTextField usernameField;
	private JPasswordField passwordField, repeatField;
	private MacroHardClient mHO;
	private MainPanel mP;
	private boolean online;
	private String usernameString;
	
	public MainPanel(MacroHardClient mHO, Font nameFont, Font menuFont, ImageIcon buttonBackground,
			ImageIcon rolloverBackground) {
		super();
		online = false;
		mP = this;
		this.mHO = mHO;
		this.nameFont = nameFont;
		this.menuFont = menuFont;
		this.buttonBackground = buttonBackground;
		this.rolloverBackground = rolloverBackground;
		initializeComponents();
		createGUI();
		addEvents();
	}

	private void initializeComponents() {
		name = new JLabel("Macrohard Word", JLabel.CENTER);
		optionsLoginButton = new JButton("Login", buttonBackground);
		signUpButton = new JButton("Sign Up", buttonBackground);
		offlineButton = new JButton("Offline", buttonBackground);
		loginLoginButton = new JButton("Login", buttonBackground);
		signUpLoginButton = new JButton("Login", buttonBackground);
		backButton = new JButton("Back", buttonBackground);
		optionsPanel = new JPanel();
		loginPanel = new JPanel();
		signUpPanel = new JPanel();
		optionsUtilPanel = new JPanel();
		usernamePanel = new JPanel();
		passwordPanel = new JPanel();
		repeatPanel = new JPanel();
		username = new JLabel("Username:");
		password = new JLabel("Password:");
		repeat = new JLabel("Repeat:       ");
		usernameField = new JTextField(12);
		passwordField = new JPasswordField(12);
		repeatField = new JPasswordField(12);
	}

	private void createGUI() {
		setBackground(Color.GRAY);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		UIManager.put("OptionPane.messageFont", new FontUIResource(menuFont));
		UIManager.put("OptionPane.buttonFont", new FontUIResource(menuFont));
		
		//Style Labels
		name.setFont(nameFont);
		name.setForeground(Color.WHITE);
		name.setBorder(new EmptyBorder(mHO.getHeight() / 4, 0, 0, 0));
		name.setAlignmentX(CENTER_ALIGNMENT);
		add(name, JPanel.CENTER_ALIGNMENT);
		username.setFont(menuFont);
		username.setForeground(Color.WHITE);
		password.setFont(menuFont);
		password.setForeground(Color.WHITE);
		repeat.setFont(menuFont);
		repeat.setForeground(Color.WHITE);
		
		//Style all buttons
		JButton[] buttons = {optionsLoginButton, signUpButton, offlineButton, loginLoginButton,
				signUpLoginButton, backButton};
		Insets none = new Insets(0, 0, 0, 0);
		EmptyBorder topBorder = new EmptyBorder(10, 0, 0, 0);
		for(JButton b : buttons) {
			b.setRolloverIcon(rolloverBackground);
			b.setForeground(Color.GRAY);
			b.setHorizontalTextPosition(SwingConstants.CENTER);
			b.setMargin(none);
			b.setContentAreaFilled(false);
			b.setBorder(topBorder);
			b.setAlignmentX(Component.CENTER_ALIGNMENT);
			b.setFont(menuFont);
		}
		
		//Set up main panel
		optionsUtilPanel.setBackground(Color.GRAY);
		optionsUtilPanel.add(optionsLoginButton);
		optionsUtilPanel.add(signUpButton);
		optionsUtilPanel.setMaximumSize(optionsUtilPanel.getMinimumSize());
		optionsPanel.setBackground(Color.GRAY);
		optionsPanel.setBorder(new EmptyBorder(mHO.getHeight() / 5, 0, 0, 0));
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		optionsPanel.add(optionsUtilPanel);
		optionsPanel.add(offlineButton, JPanel.CENTER_ALIGNMENT);
		
		//Set up username panel
		usernamePanel.setBackground(Color.GRAY);
		usernamePanel.add(username);
		usernameField.setDocument(new JTextFieldLimit(12));
		usernamePanel.add(usernameField);
		usernamePanel.setMaximumSize(new Dimension(usernamePanel.getMaximumSize().width,
				usernamePanel.getMinimumSize().height));
		
		//Set up password panel
		passwordPanel.setBackground(Color.GRAY);
		passwordPanel.add(password);
		passwordField.setDocument(new JTextFieldLimit(12));
		passwordPanel.add(passwordField);
		passwordPanel.setMaximumSize(new Dimension(passwordPanel.getMaximumSize().width,
				passwordPanel.getMinimumSize().height));
		
		//Set up repeat panel
		repeatPanel.setBackground(Color.GRAY);
		repeatPanel.add(repeat);
		repeatPanel.add(repeatField);
		repeatPanel.setMaximumSize(new Dimension(repeatPanel.getMaximumSize().width,
				repeatPanel.getMinimumSize().height));
		
		//Set up login panel
		loginPanel.setBackground(Color.GRAY);
		loginPanel.setBorder(new EmptyBorder(mHO.getHeight() / 5, 0, 0, 0));
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		
		//Set up sign up panel
		signUpPanel.setBackground(Color.GRAY);
		signUpPanel.setBorder(new EmptyBorder(mHO.getHeight() / 5, 0, 0, 0));
		signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.PAGE_AXIS));
		
		add(optionsPanel);
	}

	private void addEvents() {
		//Login button functionality	
		optionsLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				loginPanel.add(usernamePanel);
				loginPanel.add(passwordPanel);
				loginPanel.add(loginLoginButton);
				loginPanel.add(backButton);
				remove(optionsPanel);
				add(loginPanel);
				repaint();
				revalidate();
			}
		});
		//Sign Up button functionality	
		signUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				signUpPanel.add(usernamePanel);
				signUpPanel.add(passwordPanel);
				signUpPanel.add(Box.createHorizontalGlue());
				signUpPanel.add(repeatPanel);
				signUpPanel.add(signUpLoginButton);
				signUpPanel.add(backButton);
				remove(optionsPanel);
				add(signUpPanel);
				repaint();
				revalidate();
			}
		});
		//Offline button functionality	
		offlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				online = false;
				mHO.setOnline(false);
				mHO.displayFileMenu();
				remove(optionsPanel);
			}
		});
		//Back button functionality
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				usernameField.setText("");
				passwordField.setText("");
				repeatField.setText("");
				remove(signUpPanel);
				remove(loginPanel);
				remove(backButton);
				add(optionsPanel);
				repaint();
				revalidate();
			}
		});
		//Sign up login button functionality
		signUpLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				online = true;
				if(usernameField.getText().equals("")) {
					JOptionPane.showMessageDialog(mHO, "Enter a username", "Sign-up Failed", JOptionPane.WARNING_MESSAGE);
					return;
				}
				char[] password = passwordField.getPassword();
				char[] repeat = repeatField.getPassword();
				boolean containsNumber = false;
				boolean containsUpperCase = false;
				boolean equal = true;
				String passwordString = "";
				for(int i = 0; i < password.length; i++) {
					if(Character.isUpperCase(password[i])) containsUpperCase = true;
					if(Character.isDigit(password[i])) containsNumber = true;
					if(password[i] != repeat[i]) equal = false;
					if(password[i] - 'z' == 0) passwordString += 'a';		//My extremely weak
					else if(password[i] - 'Z' == 0) passwordString += 'A';
					else passwordString += (char) (password[i] + 1);		//password encryption
				}
				if(containsUpperCase && containsNumber) {
					if(!equal) {
						JOptionPane.showMessageDialog(mHO, "Passwords do not match", "Sign-up Failed", JOptionPane.WARNING_MESSAGE);
					} else {
						Scanner s = null;
						try {
							s = new Scanner(new File("resources/files/Client.config"));
							s.next();
							String host = s.next();
							s.next();
							int port = Integer.parseInt(s.next());
							usernameString = usernameField.getText();
							client = new Client(mHO, UType.SIGNUP, host, port, usernameString, passwordString, mP);
							if(online) client.start();
						} catch (FileNotFoundException e) {
							System.out.println("Cannot read client config file");
						} finally {
							if(s != null) s.close();
						}
					}
				} else {
					JOptionPane.showMessageDialog(mHO, "Passwords must contain at least:\nOne number and uppercase letter",
							"Sign-up Failed", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		//Login login button functionality
		loginLoginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				online = true;
				Scanner s = null;
				try {
					s = new Scanner(new File("resources/files/Client.config"));
					s.next();
					String host = s.next();
					s.next();
					int port = Integer.parseInt(s.next());
					String passwordString = "";
					char[] password = passwordField.getPassword();
					for(char c : password) {
						if(c - 'z' == 0) passwordString += 'a';		//My extremely weak
						else if(c - 'Z' == 0) passwordString += 'A';
						else passwordString += (char) (c + 1);		//password encryption
					}
					client = new Client(mHO, UType.LOGIN, host, port, usernameField.getText(), passwordString, mP);
					if(online) client.start();
				} catch (FileNotFoundException e) {
					System.out.println("Cannot read client config file");
				} finally {
					if(s != null) s.close();
				}
			}
		});
	}
	
	public void loginFailed() {
		online = false;
		JOptionPane.showMessageDialog(mHO, "Username or password is invalid",
				"Log-in Failed", JOptionPane.ERROR_MESSAGE);
	}
	
	public void loginSuccess() {
		online = true;
		mHO.setOnline(true);
		mHO.displayFileMenu();
		remove(signUpPanel);
		remove(loginPanel);
	}
	
	public Client getClient() {
		return client;
	}
	
	public String getUsername() {
		return usernameString;
	}
	
	public void connectionLost(String title) {
		JOptionPane.showMessageDialog(mHO, "Server cannot be reached.\nProgram in offline mode.",
				title, JOptionPane.WARNING_MESSAGE);
		online = false;
		mHO.setOnline(false);
		remove(signUpPanel);
		remove(loginPanel);
		mHO.displayFileMenu();
	}
}
