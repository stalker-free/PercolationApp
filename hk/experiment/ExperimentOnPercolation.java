package hk.experiment;

import hk.Lattice;
import hk.util.Pair;

import java.util.Random;

public class ExperimentOnPercolation implements Runnable
{
	private int rows, cols;
	private double minChance, maxChance;
	private double step;
	private int countOfExperiments = 1;
	private final Statistic statistic = new Statistic();

	public ExperimentOnPercolation(Pair<Integer, Integer> size, Pair<Double, Double> chanceRange, double step)
	{
		int rows = size.getFirst(), cols = size.getLast();
		double minChance = chanceRange.getFirst(), maxChance = chanceRange.getLast();

		if( rows < 1 || cols < 1 ||
			minChance < 0.0 || minChance > 1.0 ||
			maxChance < 0.0 || maxChance > 1.0 ||
			minChance >= maxChance ||
			step <= 0.0 || step > 1.0)
		{
			throw new IllegalArgumentException();
		}

		this.rows = rows;
		this.cols = cols;
		this.minChance = minChance;
		this.maxChance = maxChance + step;
		this.step = step;
	}

	public int getRows(){
		return rows;
	}

	public int getCols(){
		return cols;
	}

	public double getMinChance(){
		return minChance;
	}

	public double getMaxChance(){
		return maxChance;
	}

	public double getStep(){
		return step;
	}

	public int getCountOfExperiments(){
		return countOfExperiments;
	}

	public void setCountOfExperiments(int countOfExperiments){
		if(countOfExperiments < 1) throw new IllegalArgumentException();
		this.countOfExperiments = countOfExperiments;
	}

	@Override
	public void run()
	{
		Random gen = new Random();
		Lattice lattice = new Lattice();
		int i, j, k;
		double current;

		// Generate random array
		double[][] cells = new double[rows][cols];

		// Run experiments
		for(k = 0 ; k < countOfExperiments ; ++k){
			// Fill array
			for(i = 0 ; i < rows ; i++){
				for(j = 0 ; j < cols ; j++){
					cells[i][j] = gen.nextDouble();
				}
			}

			// Calculate threshold
			for(current = minChance ; current < maxChance ; current += step){
				lattice.generateNewLattice(cells, current);
				lattice.clusterize();
				statistic.put(lattice.checkEdges(), current);
			}
		}
	}

	public Statistic getStatistic(){
		return statistic;
	}
}
