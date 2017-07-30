/*
 * MacroHardOffice.java
 * Abe Wheeler
 * Text editor GUI with spell check functionality.
 */

package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

import resource.UserComm;
import resource.UserComm.UType;

public class MacroHardClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private int documentsOpen;
	private boolean online;
	private LinkedList<File> files;
	private LinkedList<String> owner;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, spellCheckMenu, usersMenu;
	private JMenuItem newMenuItem, openMenuItem, saveMenuItem, closeMenuItem;
	private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem, selectAllMenuItem;
	private JMenuItem runMenuItem, configureMenuItem, addMenuItem, removeMenuItem;
	private ConfigurePanel configurePanel;
	private SpellCheckPanel spellCheckPanel;
	private MainPanel mainPanel;
	private Font nameFont, menuFont, editorFont;
	private ImageIcon buttonBackground, configButtonBackground, rolloverBackground, configRolloverBackground;
	private MacroHardClient mHO;
	
	protected File wordlist, keyboard, choice;
	protected Scanner lines;
	protected JTabbedPane documents;
	protected LinkedList<JTextArea> textAreas;
	
	//Initialize fields
	public MacroHardClient() {
		super("Macrohard Word");
		setSize(800, 600);
		mHO = this;
		wordlist = new File("resources/files/wordlist.wl");
		keyboard = new File("resources/files/qwerty-us.kb");
		buttonBackground = new ImageIcon("resources/img/backgrounds/lightbutton.gif");
		configButtonBackground = new ImageIcon("resources/img/backgrounds/lightbutton.gif");
		rolloverBackground = new ImageIcon("resources/img/backgrounds/darkbutton.gif");
		configRolloverBackground = new ImageIcon("resources/img/backgrounds/darkbutton.gif");
		buttonBackground.setImage(buttonBackground.getImage().getScaledInstance(90, 25, Image.SCALE_SMOOTH));
		configButtonBackground.setImage(configButtonBackground.getImage().getScaledInstance(150, 25, Image.SCALE_SMOOTH));
		rolloverBackground.setImage(rolloverBackground.getImage().getScaledInstance(90, 25, Image.SCALE_SMOOTH));
		configRolloverBackground.setImage(configRolloverBackground.getImage().getScaledInstance(150, 25, Image.SCALE_SMOOTH));
		documentsOpen = 0;
		lines = null;
		choice = null;
		files = new LinkedList<File>();
		owner = new LinkedList<String>();
		textAreas = new LinkedList<JTextArea>();
		
		//Initialize fonts
		try {
			nameFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/kenvector_future.ttf")).deriveFont(36f);
			menuFont = nameFont.deriveFont(12f);
			editorFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/kenvector_future_thin.ttf")).deriveFont(12f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/kenvector_future.ttf")));
		} catch (IOException|FontFormatException e) {
			System.out.println("Error setting font, using defaults");
			Font defaultFont = new Font("Times New Roman", Font.TRUETYPE_FONT, 12);
			nameFont = defaultFont.deriveFont(36f);
			menuFont = defaultFont;
			editorFont = defaultFont;
		}
		
		//Set UI for tabbed pane before it is initialized
		UIManager.put("TabbedPane.selected",ColorUIResource.DARK_GRAY);
		UIManager.put("TabbedPane.light", ColorUIResource.DARK_GRAY);
		UIManager.put("TabbedPane.selectHighlight", ColorUIResource.DARK_GRAY);
		UIManager.put("TabbedPane.borderHightlightColor", ColorUIResource.DARK_GRAY);
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		
		initializeComponents();
		createGUI();
		addEvents();
		repaint();
	}
	
	//Initialize GUI components
	private void initializeComponents() {
		//Initialize menu bar
		menuBar = new JMenuBar() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(Toolkit.getDefaultToolkit().getImage("resources/img/menu/gray_fade.png"),0,0,getWidth(),getHeight(),this);
			}
		};
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		spellCheckMenu = new JMenu("SpellCheck");
		usersMenu = new JMenu("Users");
		
		//Initialize file menu
		newMenuItem = new JMenuItem("New");
		openMenuItem = new JMenuItem("Open");
		saveMenuItem = new JMenuItem("Save");
		closeMenuItem = new JMenuItem("Close");
		
		//Initialize edit menu
		cutMenuItem = new JMenuItem("Cut");
		copyMenuItem = new JMenuItem("Copy");
		pasteMenuItem = new JMenuItem("Paste");
		selectAllMenuItem = new JMenuItem("Select All");
		
		//Initialize spell check menu
		runMenuItem = new JMenuItem("Run");
		configureMenuItem = new JMenuItem("Configure");
		
		//Initialize user menu
		addMenuItem = new JMenuItem("Add");
		removeMenuItem = new JMenuItem("Remove");
	    
		//Initialize window
		mainPanel = new MainPanel(this, nameFont, menuFont, configButtonBackground, configRolloverBackground);
		spellCheckPanel = new SpellCheckPanel(this, menuFont, editorFont, buttonBackground, rolloverBackground);
		configurePanel = new ConfigurePanel(this, menuFont, editorFont, configButtonBackground, configRolloverBackground);
		documents = new JTabbedPane();
	}

	//Create GUI
	private void createGUI() {
		//Setup window
		setLocationRelativeTo(null);
		setIconImage((new ImageIcon("resources/img/icon/office.png")).getImage());
		try {
			setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("resources/img/icon/clearcursor.png").getImage(),
					new Point(0, 0), "custom cursor"));
        } catch(Exception e) {
        	System.out.println("Error setting cursor.");
        }
		
		//Create Menu Bar
		fileMenu.setMnemonic('F');
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(newMenuItem);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(openMenuItem);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveMenuItem);
		saveMenuItem.setEnabled(false);
		fileMenu.add(closeMenuItem);
		closeMenuItem.setEnabled(false);
		setJMenuBar(menuBar);
		
		//Configure Menu
		editMenu.setMnemonic('E');
		spellCheckMenu.setMnemonic('S');
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		configureMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);
		editMenu.addSeparator();
		editMenu.add(selectAllMenuItem);
		spellCheckMenu.add(runMenuItem);
		spellCheckMenu.add(configureMenuItem);
		usersMenu.add(addMenuItem);
		usersMenu.add(removeMenuItem);
		
		//Style menu items
		fileMenu.setForeground(Color.WHITE);
		fileMenu.setFont(menuFont);
		editMenu.setForeground(Color.WHITE);
		editMenu.setFont(menuFont);
		spellCheckMenu.setForeground(Color.WHITE);
		spellCheckMenu.setFont(menuFont);
		usersMenu.setForeground(Color.WHITE);
		usersMenu.setFont(menuFont);
		JMenuItem[] menuItems = {newMenuItem, openMenuItem, saveMenuItem, closeMenuItem,
				cutMenuItem, copyMenuItem, pasteMenuItem, selectAllMenuItem, runMenuItem,
				configureMenuItem, addMenuItem, removeMenuItem};
		String[] images = {"new", "open", "save", "close", "cut", "copy",
				"paste", "select", "run", "configure"};
		for(int i = 0; i < 10; i++) {
			new ImageIcon("resources/img/menuitems/" + images[i] + ".png").getImage();
			menuItems[i].setIcon(new ImageIcon("resources/img/menuitems/" + images[i] + ".png"));
			menuItems[i].setFont(menuFont);
		}
		menuItems[10].setFont(menuFont);
		menuItems[11].setFont(menuFont);
		
		//Style editor items
		add(mainPanel);
		documents.setBackground(Color.GRAY);
		documents.setForeground(Color.WHITE);
		documents.setFont(menuFont);		
	}

	//Add functionality
	private void addEvents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e)
		    {
		    	if(lines != null) lines.close();
		        if(choice != null) choice.delete();
		    }
		});
		//File > New
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(documentsOpen == 0) documentOpened();
				if(mainPanel.getClient() != null)
					addTab(null, "New", "", mainPanel.getClient().getUsername());
				else
					addTab(null, "New", "", "Anon");
			}
		});
		//File > Open
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int result = -1;
				if(online) {
					String[] options = {"Online", "Offline"};
					result = JOptionPane.showOptionDialog(mHO, "Where would you like to open the file?",
							"Open...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
							null, options, options[1]);
				}
				if(result != JOptionPane.CLOSED_OPTION) {
					if(!online || result == JOptionPane.NO_OPTION) {
						File openFile = selectFile("Open File...", null, false);
						if(openFile != null) {
							if(documentsOpen == 0) documentOpened();
							if(!addTab(openFile, "", "", mainPanel.getClient().getUsername())) closeLastTab();
						}
					} else {
						new RemoveUser(mHO, mainPanel.getClient(), menuFont, mainPanel.getClient().getUsername());
					}
				}
			}
		});
		//File > Save
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int result = -1;
				if(online) {
					String[] options = {"Online", "Offline"};
					result = JOptionPane.showOptionDialog(mHO, "Where would you like to save the file?",
							"Save...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
							null, options, options[1]);
				}
				if(result != JOptionPane.CLOSED_OPTION) {
					if(!online || result == JOptionPane.NO_OPTION) {
						File saveFile = selectFile("Save File...", 
								files.get(documents.getSelectedIndex()), true);
						if(saveFile != null) {
							FileWriter fW = null;
							try {
								fW = new FileWriter(saveFile);
								int index = documents.getSelectedIndex();
								fW.write(textAreas.get(index).getText());
								if(!documents.getTitleAt(index).equals(saveFile.getName())) {
									documents.setTitleAt(index, saveFile.getName());
									files.remove(index);
									files.add(index, saveFile);
								}
							} catch (IOException e) {
								System.out.println("Error saving file");
							} finally {
								if(fW != null) {
									try {
										fW.close();
									} catch (IOException e) {
										System.out.println("Error closing file writer");
									}
								}
							}
						}
					} else {
						ServerFileChooser sFC = new ServerFileChooser(mHO, "Save...", menuFont, mainPanel.getClient(),
								mainPanel.getUsername(), documents.getTitleAt(documents.getSelectedIndex()), null);
						String fileName = sFC.getChosen();
						String contents = textAreas.get(documents.getSelectedIndex()).getText();
						mainPanel.getClient().saveFile(mainPanel.getClient().getUsername(), fileName, contents);
						String wasSuccess = mainPanel.getClient().wasSuccess();
						while(wasSuccess == null && !sFC.cancelled()) {
							try {
								Thread.sleep(250);
								wasSuccess = mainPanel.getClient().wasSuccess();
							} catch(InterruptedException ie) {
								ie.printStackTrace();
							}
						}
						if(!sFC.cancelled()) {
							int index = documents.getSelectedIndex();
							if(!documents.getTitleAt(index).equals(fileName)) documents.setTitleAt(index, fileName);
							if(wasSuccess.equals("Y")) JOptionPane.showMessageDialog(mHO,
								    "File successfully saved", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
							else JOptionPane.showMessageDialog(mHO, "File failed to save",
									"Save Failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		//File > Close
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int index = documents.getSelectedIndex();
				documentsOpen--;
				documents.remove(index);
				files.remove(index);
				textAreas.remove(index);
				owner.remove(index);
				if(documentsOpen == 0) {
					closeLastTab();
				}
				if(documents.getSelectedIndex() != -1 &&
						owner.get(documents.getSelectedIndex()).equals(mainPanel.getClient().getUsername())) menuBar.add(usersMenu);
				else menuBar.remove(usersMenu);
				revalidate();
				repaint();
			}
		});
		//Edit > Cut
		cutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).cut();
			}
		});
		//Edit > Copy
		copyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).copy();
			}
		});
		//Edit > Paste
		pasteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).paste();
			}
		});
		//Edit > SelectAll
		selectAllMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).selectAll();
			}
		});
		//SpellCheck > Run
		runMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).setEditable(false);
				spellCheckPanel.performSpellCheck();
			    remove(configurePanel);
			    add(spellCheckPanel, BorderLayout.EAST);
			    revalidate();
			    repaint();
			}
		});
		//SpellCheck > Configure
		configureMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textAreas.get(documents.getSelectedIndex()).setEditable(true);
				remove(spellCheckPanel);
				if(lines != null) lines.close();
				if(choice != null) choice.delete();
				add(configurePanel, BorderLayout.EAST);
				revalidate();
				repaint();
			}
		});
		//Users > Add
		addMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String contents = textAreas.get(documents.getSelectedIndex()).getText();
				mainPanel.getClient().saveFile(owner.get(documents.getSelectedIndex()),
						documents.getTitleAt(documents.getSelectedIndex()), contents);
				new AddUser(mHO, mainPanel.getClient(), menuFont, mainPanel.getClient().getUsername(),
						documents.getTitleAt(documents.getSelectedIndex()));
			}
		});
		//Users > Remove
		removeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new RemoveUser(mHO, mainPanel.getClient(), menuFont, mainPanel.getClient().getUsername(),
						documents.getTitleAt(documents.getSelectedIndex()));
			}
		});
	}
	
	//Called when a document is opened via new or open
	private void documentOpened() {
		remove(mainPanel);
		menuBar.add(editMenu);
		menuBar.add(spellCheckMenu);
		saveMenuItem.setEnabled(true);
		closeMenuItem.setEnabled(true);
	}
	
	//Restructures the application if the last tab is closed
	private void closeLastTab() {
		remove(documents);
		add(mainPanel);
		menuBar.remove(editMenu);
		menuBar.remove(spellCheckMenu);
		saveMenuItem.setEnabled(false);
		closeMenuItem.setEnabled(false);
		repaint();
	}
	
	//Adds the contents of the passed file to a tab or creates an
	//empty tab if passed null.
	private boolean addTab(File f, String title, String initialContents, String ownerName) {
		owner.add(documentsOpen, ownerName);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea body = new JTextArea();
		body.setText(initialContents);
		//Adds the editing area
		if(f != null) {
			FileReader fR = null;
			try {
				fR = new FileReader(f);
				body.read(fR, "body");
				documents.addTab(f.getName(), panel);
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
				return false;
			} catch (IOException e) {
				System.out.println("Error reading file");
				return false;
			} finally {
				if(fR != null) {
					try {
						fR.close();
					} catch (IOException e) {
						System.out.println("Error closing file reader");
					}
				}
			}
		} else {
			documents.addTab(title, panel);
		}
		body.setSelectionColor(Color.GRAY);
		body.setFont(editorFont);
		textAreas.add(body);
		files.add(f);
		
		//Set up scroll panel
		JScrollPane scroll = new JScrollPane(body);
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
		scroll.getHorizontalScrollBar().setUI((new BasicScrollBarUI() {   
	        @Override
	        protected JButton createDecreaseButton(int orientation) {
	        	return makeScrollButton("left");
	        }

	        @Override    
	        protected JButton createIncreaseButton(int orientation) {
	        	return makeScrollButton("right");
	        }
	        @Override 
	        protected void configureScrollBarColors(){
	        	thumbColor = Color.GRAY;
	        	trackColor = Color.WHITE;
	        }
	    }));
		panel.add(scroll, BorderLayout.CENTER);
		documents.setSelectedIndex(documentsOpen);
		if(documentsOpen == 0) add(documents, BorderLayout.CENTER);
		documentsOpen++;
		repaint();
		revalidate();
		return true;
	}
	
	//Opens a txt file chooser and returns the file that the user selects. Takes the title of the window,
	//the name of the currently opened file if applicable, and a boolean indicating if this is a save operation.
	private File selectFile(String title, File file, boolean isSave) {
		JFileChooser fC;
		if(isSave) {
			fC = new JFileChooser(){
				private static final long serialVersionUID = 2L;

				@Override
			    public void approveSelection(){
			        File selected = getSelectedFile();
			        if(selected.isFile()){
			            int result = JOptionPane.showConfirmDialog(this, selected.getName() + 
			            		" already exists\nDo you want to replace it?", "Confirm Save as", 
			            		JOptionPane.WARNING_MESSAGE);
			            if(result == JOptionPane.YES_OPTION) {
			            	super.approveSelection();
			            	return;
			            }
			            if(result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
			            	return;
			            }
			        }
			        super.approveSelection();
			    }   
			};
		} else fC = new JFileChooser();
		fC.setDialogTitle(title);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt", "text");
		fC.setAcceptAllFileFilterUsed(false);
		fC.setFileFilter(filter);
		fC.setCurrentDirectory(file);
		fC.setSelectedFile(file);
		int result;
		if(isSave) result = fC.showSaveDialog(this);
		else result = fC.showOpenDialog(this);
		repaint();
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fC.getSelectedFile();
		    return selectedFile;
		}
		return null;
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
	
	protected void displayFileMenu() {
		menuBar.add(fileMenu);
		revalidate();
	}
	
	protected void removeSpellCheckPanel() {
		remove(spellCheckPanel);
		lines.close();
		choice.delete();
		revalidate();
		textAreas.get(documents.getSelectedIndex()).setEditable(true);
	}
	
	protected void removeConfigPanel() {
		remove(configurePanel);
		revalidate();
	}
	
	public void setOnline(boolean o) {
		online = o;
		if(online) {
			documents.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					if(documents.getSelectedIndex() != -1 &&
							owner.get(documents.getSelectedIndex()).equals(mainPanel.getClient().getUsername())) menuBar.add(usersMenu);
					else menuBar.remove(usersMenu);
					revalidate();
					repaint();
				}
			});
			
		}
	}
	
	public void kicked(String username, String collab, String filename) {
		for(int i = 0; i < documents.getTabCount(); i++) {
			if(documents.getTitleAt(i).equals(filename)) {
				if(owner.get(i).equals(username)) {
					JOptionPane.showMessageDialog(mHO, "You have been removed",
							"Removed", JOptionPane.WARNING_MESSAGE);
					owner.set(i, collab);
					menuBar.add(usersMenu);
					revalidate();
					repaint();
				}
			}
		}
	}
	
	public void save() {
		if(documents.getSelectedIndex() != -1) {
			for(int i = 0; i < documents.getTabCount(); i++) {
				String contents = textAreas.get(i).getText();
				mainPanel.getClient().saveFile(owner.get(i), documents.getTitleAt(i), contents);
			}
		}
	}
	
	public void myFiles() {
		ServerFileChooser sFC = new ServerFileChooser(mHO, "Open...", menuFont, mainPanel.getClient(), mainPanel.getUsername(), "", null);
		String fileName = sFC.getChosen();
		String fileContents = mainPanel.getClient().getFileContents();
		while(fileContents == null && !sFC.cancelled()) {
			try {
				Thread.sleep(250);
				fileContents = mainPanel.getClient().getFileContents();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		if(!sFC.cancelled()) {
			mainPanel.getClient().reset();
			if(documentsOpen == 0) documentOpened();
			addTab(null, fileName, fileContents, mainPanel.getClient().getUsername());
			save();
		}
		mainPanel.getClient().reset();
	}
	
	public void selectUser(String user) {
		ServerFileChooser sFC = new ServerFileChooser(mHO, "Open...", menuFont, mainPanel.getClient(), mainPanel.getClient().getUsername(), "", user);
		String chosen = sFC.getChosen();
		if(chosen != null) {
			mainPanel.getClient().request(new UserComm(UType.FILESELECTED, user, chosen));
			String fileContents = mainPanel.getClient().getFileContents();
			while(fileContents == null && !sFC.cancelled()) {
				try {
					Thread.sleep(250);
					fileContents = mainPanel.getClient().getFileContents();
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			if(!sFC.cancelled()) {
				if(documentsOpen == 0) documentOpened();
				if(!addTab(null, chosen, fileContents, user)) closeLastTab();
			}
		}
	}
	
	public String getUsername() {
		return mainPanel.getClient().getUsername();
	}
	
	public Map<String[], String> getUpdate() {
		Map<String[], String> update = new HashMap<String[], String>();
		for(int i = 0; i < textAreas.size(); i++) {
			String[] tab = new String[2];
			tab[0] = documents.getTitleAt(i);
			tab[1] = owner.get(i);
			update.put(tab, textAreas.get(i).getText());
		}
		return update;
	}
	
	public void updateDoc(String fileName, String docOwner, String contents) {
		int caretPosition = textAreas.get(documents.getSelectedIndex()).getCaretPosition();
		for(int i = 0; i < textAreas.size(); i++) {
			if(documents.getTitleAt(i).equals(fileName) && owner.get(i).equals(docOwner)) {
				textAreas.get(i).setText(contents);
			}
		}
		textAreas.get(documents.getSelectedIndex()).setCaretPosition(caretPosition);		
	}
	
	//Launches GUI
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		MacroHardClient mHO = new MacroHardClient();
		mHO.setVisible(true);
	}
}