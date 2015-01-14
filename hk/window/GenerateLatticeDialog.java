package hk.window;

import hk.Cell;
import hk.HoshenKopelman;
import hk.IntegerCell;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.Random;

public class GenerateLatticeDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private HoshenKopelman algorithm;
	private JTextField xField;
	private JTextField yField;
	private JTextField randomSeedField;
	private JTextField chanceField;

	public GenerateLatticeDialog(Toolkit toolkit){
		setContentPane(contentPane);
		setResizable(false);
		setTitle("Generate new lattice...");
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){onOK();}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){onCancel();}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		Dimension dimension = toolkit.getScreenSize();
		setLocation(dimension.width / 4, dimension.height / 4);
		pack();
	}

	private double convert(Document doc) throws BadLocationException{
		return Double.valueOf(doc.getText(0, doc.getLength()));
	}

	private void onOK(){
		try{
			// Prepare parameters
			Document doc = xField.getDocument();
			int rows = (int)convert(doc);

			doc = yField.getDocument();
			int cols = (int)convert(doc);

			doc = randomSeedField.getDocument();
			Random gen = (doc.getLength() > 0) ? new Random((int)convert(doc)) : new Random();

			doc = chanceField.getDocument();
			double chance = convert(doc);

			// Generate random array
			Cell[][] cells = new Cell[rows][cols];

			for(int i = 0 ; i < rows ; i++){
				for(int j = 0 ; j < cols ; j++){
					cells[i][j] = new IntegerCell((gen.nextDouble() < chance) ? 1 : 0);
				}
			}

			// Now send it to the algorithm
			algorithm = new HoshenKopelman(cells);

			// Leave the dialog
			dispose();
		}
		catch(BadLocationException e){
			JOptionPane.showMessageDialog(this, "Access violation: attempt to read outside of the text.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Nonnumerical symbol was entered.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void onCancel(){
		dispose();
	}

	public HoshenKopelman getAlgorithm(){
		return algorithm;
	}
}