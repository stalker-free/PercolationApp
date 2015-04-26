package hk.window;

import hk.Lattice;
import hk.util.LatticeParser;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Calendar;
import java.util.regex.Pattern;

public class PercolationApp extends JFrame
{
	private final static Toolkit toolkit = Toolkit.getDefaultToolkit();
	private final Title title = new Title();
	private final JTextArea textArea;
	private Document currentDocument;
	private JFileChooser fileDialog;
	private Lattice lattice;

	public PercolationApp()
	{
		// Set action on close the window
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Set system look and feel to the application
		setSystemLookAndFeel();

		// Initialise the file dialog
		fileDialog = new JFileChooser(".");

		// Initialise menu bar
		JMenuBar menu = new JMenuBar();
		// Initialise menus of the application
		JMenu fileMenu = makeFileMenu();
		JMenu editMenu = makeEditMenu();
		// Add menus to the menu bar
		menu.add(fileMenu);
		menu.add(editMenu);
		// Add the menu bar to the frame
		setJMenuBar(menu);

		// Initialise the text area
		textArea = new JTextArea();
		Font font = new Font("TimesRoman", Font.PLAIN, 16);
		textArea.setFont(font);
		textArea.setEditable(false);
		// Add the text area to the frame
		add(new JScrollPane(textArea));

		// Set size and location of the window
		Dimension dimension = toolkit.getScreenSize();
		setSize(dimension.width / 2, dimension.height / 2);
		setLocation(dimension.width / 4, dimension.height / 4);

		// Set title of the window
		setTitle(title.getTitle());

		// Set the new document
		currentDocument = textArea.getDocument();
	}

	/**
	 * Generate new lattice from entered options.
	 */
	private void newFile()
	{
		GenerateLatticeDialog dlg = new GenerateLatticeDialog(toolkit);
		dlg.setVisible(true);
		lattice = dlg.getLattice();
	}

	/**
	 * Load lattice from file.
	 * @param path file destination.
	 */
	private void loadFile(String path)
	{
		try
		{
			// Load data from file
			BufferedReader reader = new BufferedReader(new FileReader(path));
			StringBuffer file = new StringBuffer();
			String line, regex = "^[,\\d\\s]";
			while(reader.ready())
			{
				line = reader.readLine();
				if(Pattern.matches(regex, line))
				{
					// Prevent loading from that file
					throw new IllegalArgumentException("Incorrect file.");
				}
				file.append(line).append(";");
			}

			// Create lattice
			lattice = LatticeParser.parse(file);

			// Print message
			writeToTextArea("File has loaded successfully.");
		}
		catch(BadLocationException | IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error has occur while loading lattice.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch(IllegalArgumentException e)
		{
			JOptionPane.showMessageDialog(null, "Attempt to load wrong/corrupted file.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Set the new document
		currentDocument = textArea.getDocument();

		// Set the title of the application
		title.setTitle(path);
		setTitle(title.getTitle());
	}

	private void launchAnExperiment()
	{
		LaunchAnExperimentDialog dlg = new LaunchAnExperimentDialog(toolkit);
		dlg.setVisible(true);
		try
		{
			writeToTextArea(getTimeElapsedString(dlg.getTimeElapsed()));
		}
		catch(BadLocationException ex)
		{
			ex.printStackTrace();
		}
	}

	protected final void setSystemLookAndFeel()
	{
		// Make the application decorated
		setDefaultLookAndFeelDecorated(true);

		// Set system theme to to application
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException | InstantiationException |
				IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}

	private JMenu makeFileMenu()
	{
		// Construct the menu object
		final JMenu fileMenu = new JMenu("File");
		// Initialise items
		JMenuItem newDoc = new JMenuItem("New");
		JMenuItem openDoc = new JMenuItem("Open");
		JMenuItem launchExperiment = new JMenuItem("Launch an experiment...");
		JMenuItem exit = new JMenuItem("Exit");

		// Add listeners to the menu items
		newDoc.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				newFile();
			}
		});

		openDoc.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(fileDialog.showOpenDialog((Component)e.getSource()) ==
						JFileChooser.APPROVE_OPTION)
				{
					loadFile(fileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});

		launchExperiment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				launchAnExperiment();
			}
		});

		exit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		// Add items to the File menu
		fileMenu.add(newDoc);
		fileMenu.add(openDoc);
		fileMenu.addSeparator();
		fileMenu.add(launchExperiment);
		fileMenu.addSeparator();
		fileMenu.add(exit);

		return fileMenu;
	}

	private JMenu makeEditMenu()
	{
		// Construct the menu object
		final JMenu editMenu = new JMenu("Edit");
		// Initialise items
		JMenuItem findClusters = new JMenuItem("Find clusters");
		JMenuItem showResultLattice = new JMenuItem("Show result lattice");

		// Add listeners to the menu items
		findClusters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(lattice == null)
				{
					JOptionPane.showMessageDialog((JComponent)e.getSource(), "Lattice isn't initialised.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Start the algorithm
				Calendar calendar = Calendar.getInstance();
				long timeElapsed = -calendar.getTimeInMillis();
				lattice.clusterize();
				calendar = Calendar.getInstance();
				timeElapsed += calendar.getTimeInMillis();

				try
				{
					writeToTextArea(getTimeElapsedString(timeElapsed));
				}
				catch(BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		showResultLattice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(lattice == null)
				{
					JOptionPane.showMessageDialog((JComponent)e.getSource(),
							"Lattice isn't initialised.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try
				{
					writeToTextArea(lattice.toString());
				}
				catch(BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		// Add items to the File menu
		editMenu.add(findClusters);
		editMenu.add(showResultLattice);

		return editMenu;
	}

	private void writeToTextArea(CharSequence seq) throws BadLocationException
	{
		// Erase the content of the document object
		currentDocument.remove(0, currentDocument.getLength());
		// Insert new text to the document
		currentDocument.insertString(0, seq.toString(), null);
		// Show the content of the document in the text area
		textArea.setText(currentDocument.getText(0, currentDocument.getLength()));
		// Show the caret
		textArea.getCaret().setVisible(true);
	}

	private String getTimeElapsedString(long timeElapsed){
		return "Time elapsed during operation: " + timeElapsed + " ms" + System.lineSeparator();
	}
}
