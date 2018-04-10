package wordProcessor;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

public class WordProcessor {

	GraphicsDevice device;

	private JFrame frmWordProcessor;
	private JTextArea textArea;

	private JFileChooser fileChooser;

	private FileManagement fm;
	private Print print;
	private Statistics stats;
	private History h;
	private Formatting format;
	private Preferences p;
	private FindAndReplace fr;

	private String[] fonts;

	private int fontSize;
	private String currentFont;
	private String laF;

	private Timer saveTimer;

	private boolean fullscreen = false;

	private int autoSaveTimer = 305;

	private String savedText = "";

	private String search = "";

	private boolean fileSaved;
	private String fileName = "";

	private boolean findPressed;

	private UndoManager undoManager;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WordProcessor window = new WordProcessor();
					window.frmWordProcessor.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WordProcessor() {

		fileChooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Text Filter", "txt");
		fileChooser.setFileFilter(filter);

		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

		fm = new FileManagement();
		print = new Print();
		stats = new Statistics();
		h = new History();
		format = new Formatting();
		fr = new FindAndReplace();

		fonts = format.getAllFonts();

		undoManager = new UndoManager();

		p = new Preferences();
		p.createConfig();
		p.readPreferences();

		currentFont = p.getFont();
		fontSize = p.getSize();
		laF = p.getLaF();
		h.createHistory();

		shortcutKeys();

		initialize();

		startTimer();

		changeLaf(frmWordProcessor, laF);

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWordProcessor = new JFrame();
		frmWordProcessor.setTitle("Untitled - Word Processor");
		frmWordProcessor.setBounds(100, 100, 1200, 800);
		frmWordProcessor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWordProcessor.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1184, 0 };
		gridBagLayout.rowHeights = new int[] { 35, 50, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		frmWordProcessor.getContentPane().setLayout(gridBagLayout);

		JMenuBar menuBar = new JMenuBar();
		GridBagConstraints gbc_menuBar = new GridBagConstraints();
		gbc_menuBar.insets = new Insets(0, 0, 5, 0);
		gbc_menuBar.fill = GridBagConstraints.BOTH;
		gbc_menuBar.gridx = 0;
		gbc_menuBar.gridy = 0;
		frmWordProcessor.getContentPane().add(menuBar, gbc_menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newDocument();
				textArea.requestFocus();
			}
		});
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choosingFile();
				textArea.requestFocus();
			}
		});
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDocument();
				textArea.requestFocus();
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				namingFile();
				textArea.requestFocus();
			}
		});
		mnFile.add(mntmSaveAs);

		JMenuItem mntmPrint = new JMenuItem("Print");
		mntmPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print.print(textArea);
				textArea.requestFocus();
			}
		});

		JMenuItem mntmRecentlyOpened = new JMenuItem("Recently Opened");
		mntmRecentlyOpened.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayHistory();
				textArea.requestFocus();
			}
		});
		mnFile.add(mntmRecentlyOpened);
		mnFile.add(mntmPrint);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		mnFile.add(mntmQuit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotUndoException ex) {
					ex.printStackTrace();
				}
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmUndo);

		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotUndoException ex) {
					ex.printStackTrace();
				}
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmRedo);

		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.cut();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmCut);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.copy();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.paste();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmPaste);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.selectAll();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmSelectAll);

		JMenuItem mntmFind = new JMenuItem("Find");
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				displayFind();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmFind);

		JMenuItem mntmFindNext = new JMenuItem("Find Next");
		mntmFindNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fr.findWord(textArea, search) == true) {
					fr.selectWord(textArea, search);
					textArea.requestFocus();
				}
			}
		});
		mnEdit.add(mntmFindNext);

		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayPreferences();
				textArea.requestFocus();
			}
		});

		JMenuItem mntmReplace = new JMenuItem("Replace");
		mntmReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayReplace();
				textArea.requestFocus();
			}
		});
		mnEdit.add(mntmReplace);
		mnEdit.add(mntmPreferences);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenuItem mntmStatistics = new JMenuItem("Statistics");
		mntmStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stats.calculateStatistics(textArea);
				stats.displayStatistics();
				textArea.requestFocus();
			}
		});
		mnView.add(mntmStatistics);

		JMenuItem mntmToggleFullscreen = new JMenuItem("Toggle Fullscreen");
		mntmToggleFullscreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goFullscreen();
			}
		});
		mnView.add(mntmToggleFullscreen);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				displayHelp();
				textArea.requestFocus();
			}
		});
		mnHelp.add(mntmHelp);

		JPanel pnlFormat = new JPanel();
		GridBagConstraints gbc_pnlFormat = new GridBagConstraints();
		gbc_pnlFormat.insets = new Insets(0, 0, 5, 0);
		gbc_pnlFormat.fill = GridBagConstraints.VERTICAL;
		gbc_pnlFormat.gridx = 0;
		gbc_pnlFormat.gridy = 1;
		frmWordProcessor.getContentPane().add(pnlFormat, gbc_pnlFormat);
		GridBagLayout gbl_pnlFormat = new GridBagLayout();
		gbl_pnlFormat.columnWidths = new int[] { 79, 79, 79, 79, 55, 50, 50, 231, 50, 175, 79, 79, 0 };
		gbl_pnlFormat.rowHeights = new int[] { 34, 0 };
		gbl_pnlFormat.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_pnlFormat.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pnlFormat.setLayout(gbl_pnlFormat);

		HashMap<TextAttribute, Object> textAttrMap = new HashMap<TextAttribute, Object>();
		textAttrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

		JButton btnBold = new JButton("B");
		btnBold.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Font font = textArea.getFont();
				if (font.isBold() & font.isItalic()) {
					textArea.setFont(new Font(currentFont, Font.PLAIN + Font.ITALIC, fontSize));
				} else if (font.isBold() & !font.isItalic()) {
					textArea.setFont(new Font(currentFont, Font.PLAIN, fontSize));
				} else if (font.isItalic()) {
					textArea.setFont(new Font(currentFont, Font.BOLD + Font.ITALIC, fontSize));
				} else {
					textArea.setFont(new Font(currentFont, Font.BOLD, fontSize));
				}
				textArea.requestFocus();
			}
		});

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDocument();
				textArea.requestFocus();
			}
		});

		JButton btnNewButton = new JButton("New");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newDocument();
				textArea.requestFocus();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		pnlFormat.add(btnNewButton, gbc_btnNewButton);
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.BOTH;
		gbc_btnSave.insets = new Insets(0, 0, 0, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 0;
		pnlFormat.add(btnSave, gbc_btnSave);

		JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print.print(textArea);
				textArea.requestFocus();
			}
		});

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				choosingFile();
				textArea.requestFocus();
			}
		});
		btnOpen.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.fill = GridBagConstraints.BOTH;
		gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
		gbc_btnOpen.gridx = 2;
		gbc_btnOpen.gridy = 0;
		pnlFormat.add(btnOpen, gbc_btnOpen);
		btnPrint.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnPrint = new GridBagConstraints();
		gbc_btnPrint.fill = GridBagConstraints.BOTH;
		gbc_btnPrint.insets = new Insets(0, 0, 0, 5);
		gbc_btnPrint.gridx = 3;
		gbc_btnPrint.gridy = 0;
		pnlFormat.add(btnPrint, gbc_btnPrint);
		btnBold.setFont(new Font("Calibri", Font.BOLD, 20));
		GridBagConstraints gbc_btnBold = new GridBagConstraints();
		gbc_btnBold.fill = GridBagConstraints.BOTH;
		gbc_btnBold.insets = new Insets(0, 0, 0, 5);
		gbc_btnBold.gridx = 5;
		gbc_btnBold.gridy = 0;
		pnlFormat.add(btnBold, gbc_btnBold);

		JButton btnSettings = new JButton("Settings");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayPreferences();
				textArea.requestFocus();
			}
		});

		JButton btnItalic = new JButton("I");
		btnItalic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Font font = textArea.getFont();
				if (font.isItalic() & font.isBold()) {
					textArea.setFont(new Font(currentFont, Font.PLAIN + Font.BOLD, fontSize));
				} else if (font.isItalic() & !font.isBold()) {
					textArea.setFont(new Font(currentFont, Font.PLAIN, fontSize));
				} else if (font.isBold()) {
					textArea.setFont(new Font(currentFont, Font.ITALIC + Font.BOLD, fontSize));
				} else {
					textArea.setFont(new Font(currentFont, Font.ITALIC, fontSize));
				}
				textArea.requestFocus();
			}
		});
		btnItalic.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		GridBagConstraints gbc_btnItalic = new GridBagConstraints();
		gbc_btnItalic.fill = GridBagConstraints.BOTH;
		gbc_btnItalic.insets = new Insets(0, 0, 0, 5);
		gbc_btnItalic.gridx = 6;
		gbc_btnItalic.gridy = 0;
		pnlFormat.add(btnItalic, gbc_btnItalic);

		final JComboBox<String> cmbFonts = new JComboBox<String>();
		cmbFonts.setSelectedItem(currentFont);
		GridBagConstraints gbc_cmbFonts = new GridBagConstraints();
		gbc_cmbFonts.anchor = GridBagConstraints.NORTH;
		gbc_cmbFonts.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbFonts.insets = new Insets(0, 0, 0, 5);
		gbc_cmbFonts.gridx = 7;
		gbc_cmbFonts.gridy = 0;
		pnlFormat.add(cmbFonts, gbc_cmbFonts);
		for (int i = 0; i < fonts.length; i++) {
			if (!fonts[i].contains("Bold")) {
				cmbFonts.addItem(fonts[i]);
			}
		}
		cmbFonts.setSelectedItem(currentFont);
		cmbFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentFont = (String) cmbFonts.getSelectedItem();
				textArea.setFont(new Font(currentFont, Font.PLAIN, fontSize));
				textArea.requestFocus();
			}
		});

		final JComboBox<Integer> cmbSize = new JComboBox<Integer>();
		cmbSize.addItem(10);
		cmbSize.addItem(11);
		cmbSize.addItem(12);
		cmbSize.addItem(14);
		cmbSize.addItem(16);
		cmbSize.addItem(18);
		cmbSize.addItem(20);
		cmbSize.addItem(22);
		cmbSize.addItem(28);
		cmbSize.addItem(36);
		cmbSize.setSelectedItem(fontSize);
		GridBagConstraints gbc_cmbSize = new GridBagConstraints();
		gbc_cmbSize.anchor = GridBagConstraints.NORTH;
		gbc_cmbSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbSize.insets = new Insets(0, 0, 0, 5);
		gbc_cmbSize.gridx = 8;
		gbc_cmbSize.gridy = 0;
		pnlFormat.add(cmbSize, gbc_cmbSize);
		cmbSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				fontSize = (Integer) cmbSize.getSelectedItem();
				textArea.setFont(new Font(currentFont, Font.PLAIN, fontSize));
				textArea.requestFocus();
			}
		});
		btnSettings.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnSettings = new GridBagConstraints();
		gbc_btnSettings.fill = GridBagConstraints.BOTH;
		gbc_btnSettings.insets = new Insets(0, 0, 0, 5);
		gbc_btnSettings.gridx = 10;
		gbc_btnSettings.gridy = 0;
		pnlFormat.add(btnSettings, gbc_btnSettings);

		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayHelp();
				textArea.requestFocus();
			}
		});
		btnHelp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.fill = GridBagConstraints.BOTH;
		gbc_btnHelp.gridx = 11;
		gbc_btnHelp.gridy = 0;
		pnlFormat.add(btnHelp, gbc_btnHelp);

		JScrollPane scrollPaneText = new JScrollPane();
		GridBagConstraints gbc_scrollPaneText = new GridBagConstraints();
		gbc_scrollPaneText.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneText.gridx = 0;
		gbc_scrollPaneText.gridy = 2;
		frmWordProcessor.getContentPane().add(scrollPaneText, gbc_scrollPaneText);

		textArea = new JTextArea();
		scrollPaneText.setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setFont(new Font(currentFont, Font.PLAIN, 12));
		textArea.getDocument().addUndoableEditListener(undoManager);

	}

	private void choosingFile() {

		if (!(savedText.equals(textArea.getText()) || !(savedText.equals("")))) {
			if (saveConfirmed() == true) {
				saveDocument();
			}
		}

		if (fullscreen == true) {
			goFullscreen();
		}

		int result = fileChooser.showOpenDialog(frmWordProcessor);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();

			System.out.println("Command Executed: open");
			fm.loadFile(f.getAbsoluteFile());

			loadFile(f);
		}

	}

	private void loadFile(File f) {

		textArea.setText(null);
		if (fm.showText() != null) {
			System.out.println(fm.showText());
			textArea.append(fm.showText().toString());
		}
		fileName = f.getAbsolutePath();
		fileSaved = true;
		h.addFileToHistory(f.getAbsoluteFile().toString());
		frmWordProcessor.setTitle(f.getName() + " - Word Processor");
		savedText = textArea.getText();
		textArea.requestFocus();
	}

	private void namingFile() {
		if (fullscreen == true) {
			goFullscreen();
		}

		int result = fileChooser.showSaveDialog(frmWordProcessor);
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			savedText = textArea.getText();
			fm.saveFile(f.getAbsolutePath() + ".txt", textArea.getText());

			fileName = f.getAbsolutePath();
			h.addFileToHistory(f.getAbsoluteFile().toString());

			System.out.println(f.getName());
			frmWordProcessor.setTitle(f.getName() + " - Word Processor");
			fileSaved = true;
			textArea.requestFocus();
		} else if (result == JFileChooser.CANCEL_OPTION) {
			System.out.println("Cancel was selected");
			if (!(savedText.equals(textArea.getText()))) {
				fileSaved = false;
			}
		}
	}

	private void saveDocument() {
		if (fileSaved == true) {
			savedText = textArea.getText();
			fm.saveFile(fileName, textArea.getText());
			textArea.requestFocus();
		} else {
			namingFile();
		}
	}

	private void changeLaf(JFrame frame, String laf) {
		if (laf.equals("Metal")) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		if (laf.equals("Nimbus")) {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		if (laf.equals("Motif")) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		if (laf.equals("System Default")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		textArea.requestFocus();
		SwingUtilities.updateComponentTreeUI(frame);
	}

	private void startTimer() {
		saveTimer = new Timer();
		saveTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (fullscreen == true) {
					goFullscreen();
				}
				if (fileSaved == false || !(savedText.equals(""))) {
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"You haven't saved your document yet. Would you like to save?", "Auto Save",
							JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						namingFile();
						fileSaved = true;
					} else {
						fm.saveFile("documentBackup.txt", textArea.getText());
					}
				} else {
					savedText = textArea.getText();
					fm.saveFile(fileName, textArea.getText());
				}
			}

		}, (autoSaveTimer * 1000), (autoSaveTimer * 1000));

	}

	private void shortcutKeys() {
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (fullscreen == true) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						goFullscreen();
						return true;
					}
				}
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					if (e.isControlDown() || e.isAltDown()) {
						if (e.getKeyCode() == KeyEvent.VK_N) {
							newDocument();
							textArea.requestFocus();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_O) {
							choosingFile();
							textArea.requestFocus();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_S) {
							saveDocument();
							textArea.requestFocus();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_H) {
							displayHistory();
							textArea.requestFocus();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_P) {
							print.print(textArea);
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_Q) {
							quit();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_Z) {
							try {
								undoManager.undo();
							} catch (CannotUndoException ex) {
								ex.printStackTrace();
							}
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_Y) {
							try {
								undoManager.redo();
							} catch (CannotUndoException ex) {
								ex.printStackTrace();
							}
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_X) {
							textArea.cut();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_C) {
							textArea.copy();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_V) {
							textArea.paste();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_A) {
							textArea.selectAll();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_F) {
							displayFind();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_R) {
							displayReplace();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_COMMA) {
							displayPreferences();
							textArea.requestFocus();
							return true;
						} else if (e.getKeyCode() == KeyEvent.VK_SLASH) {
							stats.calculateStatistics(textArea);
							stats.displayStatistics();
							textArea.requestFocus();
							return true;
						}

					} // end if
				} else if (e.getKeyCode() == KeyEvent.VK_F3) {
					if (fr.findWord(textArea, search) == true) {
						fr.selectWord(textArea, search);
					}
					textArea.requestFocus();
					return true;
				} else if (e.getKeyCode() == KeyEvent.VK_F11) {
					goFullscreen();
					return true;
				} else if (e.getKeyCode() == KeyEvent.VK_F1) {
					displayHelp();
					textArea.requestFocus();
					return true;
				}
				return false;
			}
		});
	}

	private void newDocument() {
		if ((savedText.equals(textArea.getText()) || (savedText.equals("")))) {
			textArea.setText(null);
			fileSaved = false;
		} else {
			if (saveConfirmed() == true) {
				saveDocument();
			} else {
				textArea.setText(null);
				fileSaved = false;
			}
		}
	}

	private boolean saveConfirmed() {
		if (fullscreen == true) {
			goFullscreen();
		}
		boolean saved = false;
		int dialogResult = JOptionPane.showConfirmDialog(null,
				"You haven't saved your document yet. Would you like to save changes?", "Save",
				JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION) {
			saved = true;
		}
		return saved;

	}

	private void quit() {
		if (fullscreen == true) {
			goFullscreen();
		}
		String text = textArea.getText();
		if (!(text.equals(""))) {
			if (saveConfirmed() == true) {
				saveDocument();
				System.exit(0);
			} else {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}

	private void goFullscreen() {
		if (fullscreen == false) {
			device.setFullScreenWindow(frmWordProcessor);
			fullscreen = true;
		} else {
			device.setFullScreenWindow(null);
			fullscreen = false;
		}
	}

	private void displayPreferences() {

		if (fullscreen == true) {
			goFullscreen();
		}

		final JFrame frmPref = new JFrame();
		frmPref.setTitle("Preferences");
		frmPref.setResizable(false);
		frmPref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmPref.setBounds(200, 250, 440, 270);
		frmPref.setAlwaysOnTop(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmPref.setContentPane(contentPane);
		contentPane.setLayout(null);

		final JComboBox<String> cmbLaF = new JComboBox<String>();
		cmbLaF.setBounds(291, 50, 122, 20);
		cmbLaF.addItem("System Default");
		cmbLaF.addItem("Metal");
		cmbLaF.addItem("Nimbus");
		cmbLaF.addItem("Motif");
		cmbLaF.setSelectedItem(p.getLaF());
		contentPane.add(cmbLaF);

		final JComboBox<Integer> cmbSize = new JComboBox<Integer>();
		cmbSize.setBounds(364, 100, 50, 20);
		cmbSize.addItem(10);
		cmbSize.addItem(11);
		cmbSize.addItem(12);
		cmbSize.addItem(14);
		cmbSize.addItem(16);
		cmbSize.addItem(18);
		cmbSize.addItem(20);
		cmbSize.addItem(22);
		cmbSize.addItem(28);
		cmbSize.addItem(36);
		cmbSize.setSelectedItem(p.getSize());
		contentPane.add(cmbSize);

		final JComboBox<String> cmbFont = new JComboBox<String>();
		cmbFont.setBounds(231, 150, 182, 20);
		for (int i = 0; i < fonts.length; i++) {
			if (!fonts[i].contains("Bold")) {
				cmbFont.addItem(fonts[i]);
			}
		}
		cmbFont.setSelectedItem(p.getFont());
		contentPane.add(cmbFont);

		JButton btnDefaultSettings = new JButton("Default Settings");
		btnDefaultSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbLaF.setSelectedItem("System Default");
				cmbSize.setSelectedItem(12);
				cmbFont.setSelectedItem("Arial");
			}
		});
		btnDefaultSettings.setBounds(123, 195, 135, 31);
		contentPane.add(btnDefaultSettings);

		JButton btnComfrimChanges = new JButton("Comfrim Changes");
		btnComfrimChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.changePreferences(cmbLaF.getSelectedItem().toString(), cmbSize.getSelectedItem().toString(),
						cmbFont.getSelectedItem().toString());
				changeLaf(frmWordProcessor, cmbLaF.getSelectedItem().toString());
				frmPref.dispose();
			}
		});
		btnComfrimChanges.setBounds(270, 195, 143, 31);
		contentPane.add(btnComfrimChanges);

		JLabel lblCustomize = new JLabel("Customize Look and Feel");
		lblCustomize.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCustomize.setBounds(10, 50, 174, 14);
		contentPane.add(lblCustomize);

		JLabel lblDefaultFontSize = new JLabel("Default Font Size");
		lblDefaultFontSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDefaultFontSize.setBounds(10, 100, 174, 14);
		contentPane.add(lblDefaultFontSize);

		JLabel lblDefaultFont = new JLabel("Default Font");
		lblDefaultFont.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDefaultFont.setBounds(10, 150, 174, 14);
		contentPane.add(lblDefaultFont);

		JLabel lblChangeSettings = new JLabel("Change Settings");
		lblChangeSettings.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblChangeSettings.setBounds(10, 10, 174, 23);
		contentPane.add(lblChangeSettings);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmPref.dispose();
			}
		});
		btnCancel.setBounds(38, 195, 73, 31);
		contentPane.add(btnCancel);
		frmPref.setVisible(true);
	}

	private void displayHelp() {

		if (fullscreen == true) {
			goFullscreen();
		}

		final JFrame frmHelp = new JFrame();
		frmHelp.setResizable(false);
		frmHelp.setTitle("Help");
		frmHelp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmHelp.setBounds(200, 250, 450, 450);
		frmHelp.setAlwaysOnTop(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmHelp.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblShortcutKeys = new JLabel("Shortcut Key Commands");
		lblShortcutKeys.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblShortcutKeys.setBounds(10, 20, 209, 20);
		contentPane.add(lblShortcutKeys);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmHelp.dispose();
			}
		});
		btnOk.setBounds(235, 345, 89, 31);
		contentPane.add(btnOk);

		JLabel lblNewDocument = new JLabel("New Document: Ctrl+N");
		lblNewDocument.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewDocument.setBounds(10, 90, 139, 14);
		contentPane.add(lblNewDocument);

		JLabel lblOpenDocument = new JLabel("Open Document: Ctrl+O");
		lblOpenDocument.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblOpenDocument.setBounds(10, 115, 139, 14);
		contentPane.add(lblOpenDocument);

		JLabel lblSaveDocument = new JLabel("Save Document: Ctrl+S");
		lblSaveDocument.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSaveDocument.setBounds(10, 140, 139, 14);
		contentPane.add(lblSaveDocument);

		JLabel lblViewHistory = new JLabel("Recently Opened: Ctrl+H");
		lblViewHistory.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblViewHistory.setBounds(10, 165, 139, 14);
		contentPane.add(lblViewHistory);

		JLabel lblPrintDocument = new JLabel("Print Document: Ctrl+P");
		lblPrintDocument.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPrintDocument.setBounds(10, 190, 139, 14);
		contentPane.add(lblPrintDocument);

		JLabel lblQuitProgram = new JLabel("Quit Program: Ctrl+Q");
		lblQuitProgram.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblQuitProgram.setBounds(10, 215, 139, 14);
		contentPane.add(lblQuitProgram);

		JLabel lblFileMenu = new JLabel("File Menu:");
		lblFileMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFileMenu.setBounds(10, 60, 189, 20);
		contentPane.add(lblFileMenu);

		JLabel lblEditMenu = new JLabel("Edit Menu:");
		lblEditMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblEditMenu.setBounds(235, 60, 189, 20);
		contentPane.add(lblEditMenu);

		JLabel lblUndo = new JLabel("Undo: Crtl+Z");
		lblUndo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblUndo.setBounds(235, 90, 139, 14);
		contentPane.add(lblUndo);

		JLabel lblRedoCrtly = new JLabel("Redo: Crtl+Y");
		lblRedoCrtly.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRedoCrtly.setBounds(235, 115, 139, 14);
		contentPane.add(lblRedoCrtly);

		JLabel lblCut = new JLabel("Cut: Crtl+X");
		lblCut.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCut.setBounds(235, 140, 139, 14);
		contentPane.add(lblCut);

		JLabel lblCopy = new JLabel("Copy: Crtl+C");
		lblCopy.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCopy.setBounds(235, 165, 139, 14);
		contentPane.add(lblCopy);

		JLabel lblPaste = new JLabel("Paste: Crtl+V");
		lblPaste.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPaste.setBounds(235, 190, 139, 14);
		contentPane.add(lblPaste);

		JLabel lblSelectAll = new JLabel("Select All: Crtl+A");
		lblSelectAll.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSelectAll.setBounds(235, 215, 139, 14);
		contentPane.add(lblSelectAll);

		JLabel lblFind = new JLabel("Find: Crtl+F");
		lblFind.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFind.setBounds(235, 240, 139, 14);
		contentPane.add(lblFind);

		JLabel lblFindNext = new JLabel("Find Next: F3");
		lblFindNext.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFindNext.setBounds(235, 265, 139, 14);
		contentPane.add(lblFindNext);

		JLabel lblViewMenu = new JLabel("View Menu:");
		lblViewMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblViewMenu.setBounds(10, 240, 189, 20);
		contentPane.add(lblViewMenu);

		JLabel lblStatistics = new JLabel("Statistics: Crtl+/");
		lblStatistics.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStatistics.setBounds(10, 270, 139, 14);
		contentPane.add(lblStatistics);

		JLabel lblToggleFullscreen = new JLabel("Toggle Fullscreen: F11");
		lblToggleFullscreen.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblToggleFullscreen.setBounds(10, 295, 139, 14);
		contentPane.add(lblToggleFullscreen);

		JLabel lblHelpMenu = new JLabel("Help Menu:");
		lblHelpMenu.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblHelpMenu.setBounds(10, 320, 189, 20);
		contentPane.add(lblHelpMenu);

		JLabel lblHelp = new JLabel("Help: F1");
		lblHelp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblHelp.setBounds(10, 345, 139, 14);
		contentPane.add(lblHelp);

		JLabel lblPreferences = new JLabel("Preferences: Crtl+,");
		lblPreferences.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPreferences.setBounds(235, 290, 139, 14);
		contentPane.add(lblPreferences);
		frmHelp.setVisible(true);

	}

	private void displayHistory() {

		if (fullscreen == true) {
			goFullscreen();
		}
		h.readHistory();
		String[] files = h.getFiles();

		final JFrame frmHistory = new JFrame();
		frmHistory.setResizable(false);
		frmHistory.setTitle("History");
		frmHistory.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmHistory.setBounds(200, 250, 450, 200);
		frmHistory.setAlwaysOnTop(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmHistory.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblRecentlyOpenedFiles = new JLabel("Recently Opened Files");
		lblRecentlyOpenedFiles.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblRecentlyOpenedFiles.setBounds(12, 12, 199, 25);
		contentPane.add(lblRecentlyOpenedFiles);

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmHistory.dispose();
			}
		});
		btnClose.setBounds(334, 117, 98, 31);
		contentPane.add(btnClose);

		final JComboBox<String> cmbHistory = new JComboBox<String>();
		cmbHistory.setBounds(12, 63, 295, 25);
		for (int i = 0; i < files.length; i++) {
			cmbHistory.addItem(files[i]);
		}
		contentPane.add(cmbHistory);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfirmed();
				File file = new File(cmbHistory.getSelectedItem().toString());
				fm.loadFile(file);
				loadFile(file);
				frmHistory.dispose();
			}
		});
		btnLoad.setBounds(334, 60, 98, 31);
		contentPane.add(btnLoad);

		frmHistory.setVisible(true);

	}

	private void displayFind() {

		if (fullscreen == true) {
			goFullscreen();
		}

		final JFrame frmFind = new JFrame();
		frmFind.setTitle("Find");
		frmFind.setResizable(false);
		frmFind.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		frmFind.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmFind.setAlwaysOnTop(true);
		frmFind.setBounds(200, 250, 450, 159);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmFind.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblFindWhat = new JLabel("Find What?:");
		lblFindWhat.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFindWhat.setBounds(10, 33, 66, 14);
		contentPane.add(lblFindWhat);

		final JTextField txtSearch = new JTextField();
		txtSearch.setBounds(88, 30, 210, 30);
		txtSearch.setText(fr.getSearch());
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);

		JButton btnFindNext = new JButton("Find Next");
		btnFindNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fr.findWord(textArea, txtSearch.getText()) == true) {
					fr.selectWord(textArea, txtSearch.getText());
					search = txtSearch.getText();
				}
				txtSearch.requestFocus();

			}
		});
		btnFindNext.setBounds(335, 25, 98, 31);
		contentPane.add(btnFindNext);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmFind.dispose();
			}
		});
		btnCancel.setBounds(335, 66, 98, 31);
		contentPane.add(btnCancel);

		JLabel lblCase = new JLabel("(Search is case sensitive)");
		lblCase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCase.setBounds(10, 65, 146, 14);
		contentPane.add(lblCase);

		frmFind.setVisible(true);
	}

	private void displayReplace() {

		findPressed = false;

		final JFrame frmReplace = new JFrame();
		frmReplace.setResizable(false);
		frmReplace.setAlwaysOnTop(true);
		frmReplace.setTitle("Find & Replace");
		frmReplace.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		frmReplace.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmReplace.setBounds(200, 250, 450, 220);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frmReplace.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblFindWhat = new JLabel("Find What:");
		lblFindWhat.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFindWhat.setBounds(12, 33, 66, 14);
		contentPane.add(lblFindWhat);

		final JTextField txtSearch = new JTextField();
		txtSearch.setBounds(98, 30, 210, 30);
		txtSearch.setText(fr.getSearch());
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);

		final JTextField txtReplace = new JTextField();
		txtReplace.setColumns(10);
		txtReplace.setBounds(98, 70, 210, 30);
		contentPane.add(txtReplace);

		JButton btnFindNext = new JButton("Find Next");
		btnFindNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fr.findWord(textArea, txtSearch.getText()) == true) {
					fr.selectWord(textArea, txtSearch.getText());
					search = txtSearch.getText();
					findPressed = true;
				}
				txtSearch.requestFocus();
			}
		});
		btnFindNext.setBounds(335, 25, 98, 31);
		contentPane.add(btnFindNext);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmReplace.dispose();
			}
		});
		btnCancel.setBounds(335, 145, 98, 31);
		contentPane.add(btnCancel);

		JLabel lblCase = new JLabel("(Search is case sensitive)");
		lblCase.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCase.setBounds(12, 115, 146, 14);
		contentPane.add(lblCase);

		JButton btnReplace = new JButton("Replace");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (findPressed == true) {
					fr.replace(textArea, txtSearch.getText(), txtReplace.getText());
					findPressed = false;
				} else {
					JOptionPane.showMessageDialog(null, "You haven't searched yet, pressed find next", "Find",
							JOptionPane.ERROR_MESSAGE);
				}

				txtReplace.requestFocus();
			}
		});
		btnReplace.setBounds(335, 65, 98, 31);
		contentPane.add(btnReplace);

		JButton btnReplaceAll = new JButton("Replace All");
		btnReplaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fr.findWord(textArea, txtSearch.getText()) == true) {
					fr.replaceAll(textArea, txtReplace.getText());
				}
				txtReplace.requestFocus();
			}
		});
		btnReplaceAll.setBounds(334, 105, 98, 31);
		contentPane.add(btnReplaceAll);

		JLabel lblReplaceWith = new JLabel("Replace With:");
		lblReplaceWith.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblReplaceWith.setBounds(12, 73, 76, 14);
		contentPane.add(lblReplaceWith);

		frmReplace.setVisible(true);
	}

}
