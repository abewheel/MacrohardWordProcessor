package client;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import resource.UserComm;
import resource.UserComm.UType;

public class AddUser extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private Client client;
	private Font menuFont;
	private JLabel addUserLabel;
	private JPanel labelPanel, buttonPanel;
	private JTextField usernameField;
	private JButton okButton, cancelButton;
	private String username, fileName;
	private AddUser addUser;
	
	public AddUser(MacroHardClient mHO, Client client, Font menuFont, String username,
			String fileName) {
		super(mHO, "Add User...", Dialog.ModalityType.DOCUMENT_MODAL);
		this.client = client;
		this.menuFont = menuFont;
		this.username = username;
		this.fileName = fileName;
		addUser = this;
		initializeComponents();
		createGUI();
		addEvents();
		setVisible(true);
	}
	
	private void initializeComponents() {
		labelPanel = new JPanel();
		buttonPanel = new JPanel();
		addUserLabel = new JLabel("Share Document With:");
		usernameField = new JTextField();
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
	}
	
	private void createGUI() {
		setSize(400, 150);
		setLocationRelativeTo(null);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		labelPanel.setLayout(new FlowLayout());
		labelPanel.add(addUserLabel);
		addUserLabel.setFont(menuFont);
		addUserLabel.setBorder(new EmptyBorder(10,0,10,0));
		usernameField.setFont(menuFont);
		usernameField.setMaximumSize(new Dimension(350, 30));
		okButton.setFont(menuFont);
		cancelButton.setFont(menuFont);
			
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(new EmptyBorder(10,0,10,0));
		
		contentPane.add(labelPanel);
		contentPane.add(usernameField);
		contentPane.add(buttonPanel);
	}
	
	private void addEvents() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = usernameField.getText();
				if(text != "") {
					client.request(new UserComm(UType.ADDCOLLAB, username, fileName, text));
					dispose();
				}
				while(client.getCollabSuccess() == null) {
		        	try {
		        		Thread.sleep(250);
		        	} catch(InterruptedException ie) {
		        		ie.printStackTrace();
		        	}
		        };
		        if(client.getCollabSuccess().equals("N")) {
		        	JOptionPane.showMessageDialog(addUser,
		            		"Add User failed", "Add User Failed...", 
		            		JOptionPane.ERROR_MESSAGE);
		        }
		        client.reset();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}
}
