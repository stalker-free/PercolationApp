package hk.window;

import hk.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LaunchAnExperimentDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField xField;
	private JTextField yField;
	private JTextField minProbabilityField;
	private JTextField maxProbabilityField;
	private JTextField probabilityIncrementField;
	private JTextField maxExperimentCountField;
	private long timeElapsed;

	public LaunchAnExperimentDialog(Toolkit toolkit){
		setContentPane(contentPane);
		setResizable(false);
		setTitle("Run an experiment...");
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

			doc = minProbabilityField.getDocument();
			double minChance = convert(doc);

			doc = maxProbabilityField.getDocument();
			double maxChance = convert(doc);

			doc = probabilityIncrementField.getDocument();
			double step = convert(doc);

			doc = maxExperimentCountField.getDocument();
			int countOfExperiments = (int)convert(doc);

			Random gen = new Random();
			Lattice lattice = new Lattice();

			// Leave the dialog
			dispose();

			// Generate random array
			double[][] cells = new double[rows][cols];

			Calendar calendar = Calendar.getInstance();
			timeElapsed = -calendar.getTimeInMillis();
			// Run experiments
			for(int k = 0 ; k < countOfExperiments ; ++k){
				// Fill array
				for(int i = 0 ; i < rows ; i++){
					for(int j = 0 ; j < cols ; j++){
						cells[i][j] = gen.nextDouble();
					}
				}

				// Calculate threshold
				for(double current = minChance ; current < maxChance ; current += step){
					lattice.generateNewLattice(cells, current);
					lattice.clusterize();
					lattice.checkEdges();
				}
			}
			calendar = Calendar.getInstance();
			timeElapsed += calendar.getTimeInMillis();
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

	public long getTimeElapsed(){
		return timeElapsed;
	}
}
