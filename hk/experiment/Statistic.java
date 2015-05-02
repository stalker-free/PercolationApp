package hk.experiment;

import java.util.EnumMap;
import static hk.experiment.TwoDimensionalPercolation.*;

public class Statistic
{
	private EnumMap<TwoDimensionalPercolation, Double> minPercolationThresholds =
			new EnumMap<>(TwoDimensionalPercolation.class);
	private EnumMap<TwoDimensionalPercolation, Double> averagePercolationThresholds =
			new EnumMap<>(TwoDimensionalPercolation.class);
	private EnumMap<TwoDimensionalPercolation, Double> maxPercolationThresholds =
			new EnumMap<>(TwoDimensionalPercolation.class);
	private EnumMap<TwoDimensionalPercolation, Long> addends =
			new EnumMap<>(TwoDimensionalPercolation.class);

	public Statistic()
	{
		minPercolationThresholds.put(NONE, 1.0);
		minPercolationThresholds.put(BY_X, 1.0);
		minPercolationThresholds.put(BY_Y, 1.0);
		minPercolationThresholds.put(BY_XY, 1.0);
		averagePercolationThresholds.put(NONE, 0.0);
		averagePercolationThresholds.put(BY_X, 0.0);
		averagePercolationThresholds.put(BY_Y, 0.0);
		averagePercolationThresholds.put(BY_XY, 0.0);
		maxPercolationThresholds.put(NONE, 0.0);
		maxPercolationThresholds.put(BY_X, 0.0);
		maxPercolationThresholds.put(BY_Y, 0.0);
		maxPercolationThresholds.put(BY_XY, 0.0);
		addends.put(NONE, 0L);
		addends.put(BY_X, 0L);
		addends.put(BY_Y, 0L);
		addends.put(BY_XY, 0L);
	}

	public void put(TwoDimensionalPercolation key, double value)
	{
		if(key.equals(BY_XY))
		{
			this.addNewValue(BY_X, value);
			this.addNewValue(BY_Y, value);
		}

		this.addNewValue(key, value);
	}

	private double incrementMovingAverage(TwoDimensionalPercolation key, double newValue)
	{
		double prevAverage = averagePercolationThresholds.get(key);
		return prevAverage + (newValue - prevAverage) / addends.get(key);
	}

	private void addNewValue(TwoDimensionalPercolation key, double value)
	{
		addends.put(key, addends.get(key) + 1L);
		averagePercolationThresholds.put(key, incrementMovingAverage(key, value));
		if(value < minPercolationThresholds.get(key))
		{
			minPercolationThresholds.put(key, value);
		}
		if(value > maxPercolationThresholds.get(key))
		{
			maxPercolationThresholds.put(key, value);
		}
	}

	public double getMinThreshold(TwoDimensionalPercolation key)
	{
		return minPercolationThresholds.get(key);
	}

	public double getAverageThreshold(TwoDimensionalPercolation key)
	{
		return averagePercolationThresholds.get(key);
	}

	public double getMaxThreshold(TwoDimensionalPercolation key)
	{
		return maxPercolationThresholds.get(key);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String nl = System.lineSeparator();
		for(TwoDimensionalPercolation key : TwoDimensionalPercolation.values())
		{
			sb.append(key).append(':').append(nl);
			sb.append("Minimal threshold: ").append(minPercolationThresholds.get(key)).append(nl);
			sb.append("Average threshold: ").append(averagePercolationThresholds.get(key)).append(nl);
			sb.append("Maximal threshold: ").append(maxPercolationThresholds.get(key)).append(nl);
		}
		return sb.toString();
	}
}
