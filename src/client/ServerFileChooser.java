package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import resource.UserComm;
import resource.UserComm.UType;

public class ServerFileChooser extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private DefaultListModel<String> listModel;
	private Font menuFont;
	private JButton cancelButton, openSaveButton;
	private JTextField fileField;
	private JLabel selectLabel, fileLabel;
	private JList<String> fileList;
	private JPanel listPanel, filePanel, buttonPanel;
	private JScrollPane fileListScroller;
	private String openSave, chosen;
	private Client client;
	private boolean cancelled;
	private String initialFileName, sharer, username;
	
	public ServerFileChooser(MacroHardClient mHO, String openSave, Font menuFont, Client client, String username, String initialFileName, String sharer) {
		super(new Dialog(mHO), openSave, Dialog.ModalityType.DOCUMENT_MODAL);
		cancelled = false;
		listModel = new DefaultListModel<String>();
		this.initialFileName = initialFileName;
		this.openSave = openSave;
		this.client = client;
		this.menuFont = menuFont;
		this.sharer = sharer;
		this.username = username;
		if(sharer == null) client.request(new UserComm(UType.FILES, username, ""));
		setSize(600, 300);
		setLocationRelativeTo(null);
		initializeComponents();
		createGUI();
		addEvents();
		setVisible(true);
	}
	
	private void initializeComponents() {
		cancelButton = new JButton("Cancel");
		if(openSave.equals("Open...")) openSaveButton = new JButton("Open");
		else openSaveButton = new JButton("Save");
		fileField = new JTextField();
		selectLabel = new JLabel("Select A File:");
		fileLabel = new JLabel("File:");
		fileList = new JList<String>(listModel);
		listPanel = new JPanel();
		filePanel = new JPanel();
		buttonPanel = new JPanel();
		fileListScroller = new JScrollPane(fileList);
	}
	
	private void createGUI() {
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		fileList.setVisibleRowCount(-1);
		fileList.setPrototypeCellValue("file.txt\t\t");
		fileList.setFont(menuFont);
		fileList.setFixedCellWidth(150);
		
        fileListScroller.setPreferredSize(new Dimension(520, 300));
        fileLabel.setAlignmentX(LEFT_ALIGNMENT);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
        selectLabel.setLabelFor(fileList);
        listPanel.add(selectLabel);
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(fileListScroller);
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        fileField.setText(initialFileName);
        
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.LINE_AXIS));
        filePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filePanel.add(fileLabel);
        filePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        filePanel.add(fileField);
        if(sharer == null) listPanel.add(filePanel);
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(openSaveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);
        
        if(sharer == null) {
	        while(client.getFiles() == null) {
	        	try {
	        		Thread.sleep(250);
	        	} catch(InterruptedException ie) {
	        		ie.printStackTrace();
	        	}
	        }
	        for(String file : client.getFiles().keySet()) {
	        	listModel.addElement(file);
	        }
        } else {
        	client.request(new UserComm(UType.SHAREDFILES, username, sharer));
        	while(client.getSharedFiles() == null) {
	        	try {
	        		Thread.sleep(250);
	        	} catch(InterruptedException ie) {
	        		ie.printStackTrace();
	        	}
	        }
	        for(String file : client.getSharedFiles()) {
	        	listModel.addElement(file);
	        }
        }
        client.reset();
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	private void addEvents() {
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				chosen = null;
				cancelled = true;
				dispose();
			}
		});
		openSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				chosen = fileField.getText();
				if(chosen != "") {
					if(sharer == null) {
						if(openSave.equals("Open...")) client.request(new UserComm(UType.FILESELECTED, client.getUsername(), chosen));
					} else {
						
					}
					dispose();
				}
			}
		});
		fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fileField.setText(fileList.getSelectedValue());
			}
		});
	}
	
	public String getChosen() {
		return chosen;
	}
	
	public boolean cancelled() {
		return cancelled;
	}
}
