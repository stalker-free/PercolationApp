package hk.window;

import hk.experiment.ExperimentOnPercolation;
import hk.experiment.Statistic;
import hk.util.Pair;

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
	private Statistic statistic;
	private Long timeElapsed = null;

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
			ExperimentOnPercolation experiment;

			// Prepare parameters
			{
				Document doc = xField.getDocument();
				int rows = (int)convert(doc);

				doc = yField.getDocument();
				int cols = (int)convert(doc);

				Pair<Integer, Integer> size = new Pair<>(rows, cols);

				doc = minProbabilityField.getDocument();
				double minChance = convert(doc);

				doc = maxProbabilityField.getDocument();
				double maxChance = convert(doc);

				Pair<Double, Double> chanceRange = new Pair<>(minChance, maxChance);

				doc = probabilityIncrementField.getDocument();
				double step = convert(doc);

				experiment = new ExperimentOnPercolation(size, chanceRange, step);

				doc = maxExperimentCountField.getDocument();
				int countOfExperiments = (int)convert(doc);

				experiment.setCountOfExperiments(countOfExperiments);
			}

			// Leave the dialog
			dispose();

			// Run experiments
			Calendar calendar = Calendar.getInstance();
			timeElapsed = -calendar.getTimeInMillis();
			experiment.run();
			calendar = Calendar.getInstance();
			timeElapsed += calendar.getTimeInMillis();

			statistic = experiment.getStatistic();
		}
		catch(BadLocationException e){
			JOptionPane.showMessageDialog(this, "Access violation: attempt to read outside of the text.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Nonnumerical symbol was entered.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		catch(IllegalArgumentException e){
			JOptionPane.showMessageDialog(this, "A wrong parameter value was entered.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void onCancel(){
		dispose();
	}

	public Long getTimeElapsed(){
		return timeElapsed;
	}

	public Statistic getStatistic(){
		return statistic;
	}
}
