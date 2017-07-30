package client;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import resource.UserComm;
import resource.UserComm.UType;

public class RemoveUser extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private Client client;
	private DefaultListModel<String> listModel;
	private Font menuFont;
	private JButton removeButton, cancelButton;
	private JLabel userLabel;
	private JList<String> userList;
	private JPanel labelPanel, buttonsPanel;
	private JScrollPane scrollPane;
	private String username, fileName;
	
	public RemoveUser(MacroHardClient mHO, Client client, Font menuFont, String username, String fileName) {
		super(mHO, "Remove User...", Dialog.ModalityType.DOCUMENT_MODAL);
		this.menuFont = menuFont;
		this.username = username;
		this.fileName = fileName;
		this.client = client;
		initializeComponents("Remove", "Cancel");
		createGUI(true);
		addEvents(true);
		setVisible(true);
	}
	
	public RemoveUser(MacroHardClient mHO, Client client, Font menuFont, String username) {
		super(mHO, "Select User...", Dialog.ModalityType.DOCUMENT_MODAL);
		this.menuFont = menuFont;
		this.username = username;
		this.client = client;
		initializeComponents("My Files...", "Select User...");
		createGUI(false);
		addEvents(false);
		setVisible(true);
	}
	
	private void initializeComponents(String b1, String b2) {
		listModel = new DefaultListModel<String>();
		removeButton = new JButton(b1);
		cancelButton = new JButton(b2);
		labelPanel = new JPanel();
		buttonsPanel = new JPanel();
		userLabel = new JLabel("Choose a User:");
		userList = new JList<String>(listModel);
		scrollPane = new JScrollPane(userList);
	}
	
	private void createGUI(boolean isRemove) {
		setSize(500, 400);
		setLocationRelativeTo(null);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		labelPanel.setLayout(new FlowLayout());
		userLabel.setFont(menuFont);
		userLabel.setBorder(new EmptyBorder(10,0,10,0));
		labelPanel.add(userLabel);

		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setLayoutOrientation(JList.VERTICAL);
		userList.setVisibleRowCount(-1);
		userList.setPrototypeCellValue("Select User...");
		userList.setFont(menuFont);
		
		scrollPane.setPreferredSize(new Dimension(450, 300));
		scrollPane.setBorder(new EmptyBorder(0,10,10,10));
		
		Vector<String> users;
		if(isRemove) {
			client.request(new UserComm(UType.GETCOLLABS, username, fileName));
			while(client.getUsers() == null) {
	        	try {
	        		Thread.sleep(250);
	        	} catch(InterruptedException ie) {
	        		ie.printStackTrace();
	        	}
	        }
	        users = client.getUsers();
		} else {
			client.request(new UserComm(UType.GETSHARERS, username));
			while(client.getSharers() == null) {
	        	try {
	        		Thread.sleep(250);
	        	} catch(InterruptedException ie) {
	        		ie.printStackTrace();
	        	}
	        }
			users = client.getSharers();
		}
		for(String user : users) listModel.addElement(user);
		client.reset();
		
		removeButton.setFont(menuFont);
		cancelButton.setFont(menuFont);
		buttonsPanel.add(removeButton);
		buttonsPanel.add(cancelButton);
		
		contentPane.add(labelPanel);
		contentPane.add(scrollPane);
		contentPane.add(buttonsPanel);
	}
	
	private void addEvents(boolean isRemove) {
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isRemove) {
					if(!userList.isSelectionEmpty()) {
						client.request(new UserComm(UType.REMOVECOLLAB, username, userList.getSelectedValue(), fileName));
						dispose();
					}
				} else {
					client.myFiles();
					dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!userList.isSelectionEmpty()) {
					if(!isRemove) client.selectUser(userList.getSelectedValue());
				}
				dispose();
			}
		});
	}
}
