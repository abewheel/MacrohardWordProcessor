package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalComboBoxUI;

import correct.AutoCorrect;

public class SpellCheckPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private MacroHardClient mHO;
	private ImageIcon buttonBackground, rolloverBackground;
	private JButton ignoreButton, addButton, changeButton, closeButton;
	private JComboBox<String> suggestionsBox;
	private JLabel wordLabel;
	private JPanel ignoreAdd, suggestChange, filler;
	private Font menuFont, editorFont;
	
	public SpellCheckPanel(MacroHardClient mHO, Font menuFont, Font editorFont, ImageIcon buttonBackground,
			ImageIcon rolloverBackground) {
		this.mHO = mHO;
		this.menuFont = menuFont;
		this.editorFont = editorFont;
		this.buttonBackground = buttonBackground;
		this.rolloverBackground = rolloverBackground;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(CENTER_ALIGNMENT);
		setAlignmentY(TOP_ALIGNMENT);
		setBorder(new TitledBorder(new LineBorder(Color.WHITE), "Spell Check",
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, menuFont, Color.WHITE));
		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(300, mHO.getHeight()));
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents() {
		wordLabel = new JLabel();
		ignoreAdd = new JPanel();
		suggestChange = new JPanel();
		filler = new JPanel();
		ignoreButton = new JButton("Ignore", buttonBackground);
		addButton = new JButton("Add", buttonBackground);
		changeButton = new JButton("Change", buttonBackground);
		closeButton = new JButton("Close");
		suggestionsBox = new JComboBox<String>();
	}
	
	private void createGUI() {
		wordLabel.setAlignmentX(CENTER_ALIGNMENT);
		wordLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		wordLabel.setFont(editorFont.deriveFont(18f));
		wordLabel.setForeground(Color.WHITE);
		add(wordLabel);
		
		JButton[] buttons = {ignoreButton, addButton, changeButton, closeButton};
		Insets none = new Insets(0, 0, 0, 0);
		EmptyBorder noBorder = new EmptyBorder(0, 0, 0, 0);
		for(JButton b : buttons) {
			b.setFont(menuFont);
			b.setSize(90, 25);
			b.setRolloverIcon(rolloverBackground);
			b.setForeground(Color.GRAY);
			b.setHorizontalTextPosition(SwingConstants.CENTER);
			b.setMargin(none);
			b.setContentAreaFilled(false);
			b.setBorder(noBorder);
		}
		
		ignoreAdd.add(ignoreButton);
		ignoreAdd.add(addButton);
		ignoreAdd.setBackground(Color.GRAY);
		ignoreAdd.setAlignmentX(CENTER_ALIGNMENT);
		add(ignoreAdd);
		
		suggestionsBox.setFont(menuFont);
		suggestionsBox.setBorder(new LineBorder(Color.WHITE));
		suggestionsBox.setBackground(Color.WHITE);
		suggestionsBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
		    public void paint(Graphics g) {
		        setBackground(Color.GRAY);
		        setForeground(Color.WHITE);
		        super.paint(g);
		    }
		});
		suggestionsBox.setUI(new MetalComboBoxUI() {
			@Override 
			protected JButton createArrowButton() {
				ImageIcon iI = new ImageIcon("resources/img/scrollbar/down.png");
				JButton downArrow = new JButton(iI);
				downArrow.setSize(25, 25);
				downArrow.setBorder(new EmptyBorder(0, 0, 0, 0));
				downArrow.setMargin(new Insets(0, 0, 0, 0));
				downArrow.setContentAreaFilled(false);
				iI.setImage(iI.getImage().getScaledInstance(downArrow.getWidth(), downArrow.getHeight(), Image.SCALE_SMOOTH));
				ImageIcon invert = new ImageIcon("resources/img/scrollbar/downinvert.png");
				invert.setImage(invert.getImage().getScaledInstance(downArrow.getWidth(), downArrow.getHeight(), Image.SCALE_SMOOTH));
				downArrow.setRolloverIcon(invert);
				return downArrow;
			}
		});
		suggestionsBox.setPreferredSize(new Dimension(150,25));
		suggestChange.add(suggestionsBox);
		suggestChange.add(changeButton);
		suggestChange.setBackground(Color.GRAY);
		
		filler.setBackground(Color.GRAY);
		filler.setLayout(new BorderLayout());
		filler.add(suggestChange);
		add(filler);
		
		closeButton.setIcon(buttonBackground);
		closeButton.setAlignmentX(CENTER_ALIGNMENT);
		add(closeButton, BoxLayout.PAGE_AXIS);
	}
	
	private void addEvents() {
		//SpellCheck panel Ignore button	
		ignoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				nextWord();
			}
		});
		//SpellCheck panel Add button
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(mHO.wordlist, true));
					bw.write("\n" + wordLabel.getText().substring(10));
					bw.flush();
				} catch (IOException e) {
					System.out.println("Error writing to " + mHO.wordlist);
				} finally {
					if(bw != null) {
						try {
							bw.close();
						} catch (IOException e) {
							System.out.println("Error closing buffered writer");
						}
					}
				}
				nextWord();
			}
		});
		//SpellCheck panel Change button
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String contents = mHO.textAreas.get(mHO.documents.getSelectedIndex()).getText();
				contents = contents.replaceFirst("(?i)" + wordLabel.getText().substring(10), "" + suggestionsBox.getSelectedItem());
				mHO.textAreas.get(mHO.documents.getSelectedIndex()).setText(contents);
				nextWord();
			}
		});	
		//SpellCheck panel Close button
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mHO.removeSpellCheckPanel();
			}
		});
	}
	
	//Moves to the next unrecognized word during SpellCheck.
		private void nextWord() {
			if(mHO.lines.hasNextLine()) {
				suggestionsBox.removeAllItems();
				changeButton.setEnabled(true);
				Scanner words = new Scanner(mHO.lines.nextLine());
				String word = words.next();
				String contents = mHO.textAreas.get(mHO.documents.getSelectedIndex()).getText();
				int start = contents.toLowerCase().indexOf(word);
				JTextArea current = mHO.textAreas.get(mHO.documents.getSelectedIndex());
				current.requestFocus();
				current.select(start, start + word.length());
				wordLabel.setText("Spelling: " + word);
				words.next();
				if(!words.hasNext()) changeButton.setEnabled(false);
				while(words.hasNext()) {
					suggestionsBox.addItem(words.next());
				}
				words.close();
			} else {
				mHO.removeSpellCheckPanel();
			}
		}
		
		protected void performSpellCheck() {
			long time = Calendar.getInstance().getTime().getTime();
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(time + ".txt", "UTF-8");
				writer.print(mHO.textAreas.get(mHO.documents.getSelectedIndex()).getText());
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				System.out.println("Error writing spell check file");
			} finally {
				if(writer != null) writer.close();
			}
			String[] params = {mHO.keyboard.getPath(), mHO.wordlist.getPath(), time + ".txt"};
			AutoCorrect.main(params);
			(new File(time + ".txt")).delete();
			File fl = new File(System.getProperty("user.dir"));
		    File[] files = fl.listFiles(new FileFilter() {          
		        public boolean accept(File file) {
		            return file.isFile();
		        }
		    });
		    long lastMod = Long.MIN_VALUE;
		    mHO.choice = null;
		    for (File file : files) {
		        if (file.lastModified() > lastMod) {
		            mHO.choice = file;
		            lastMod = file.lastModified();
		        }
		    }
		  //Found the output file
		    if(mHO.choice != null) {
			    try {
					mHO.lines = new Scanner(new FileInputStream(mHO.choice));
					nextWord();
				} catch (FileNotFoundException e) {
					System.out.println("Error reading spell check file");
				}
		    }
		}
}
