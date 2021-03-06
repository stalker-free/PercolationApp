package hk;

import hk.cell.*;
import hk.experiment.TwoDimensionalPercolation;

import java.util.*;

/**
 * This class is created for wrapping lattice
 * and executing the Hoshen-Kopelman algorithm on it.
 */
public class Lattice
{
	private Cell[][] initialLattice;
	private Cell[][] resultLattice;
	private HoshenKopelman hk = new HoshenKopelman();
	private boolean isComputed = false;

	public Lattice()
	{
	}

	public Lattice(Cell[][] lattice)
	{
		initialLattice = new Cell[lattice.length][];
		resultLattice = new Cell[lattice.length][lattice[0].length];
		for(int i = 0 ; i < initialLattice.length ; i++)
		{
			initialLattice[i] = Arrays.copyOf(lattice[i], lattice[i].length);
			for(int j = 0 ; j < initialLattice[i].length ; j++){
				resultLattice[i][j] = new Cell();
			}
		}
	}

	public void clusterize()
	{
		hk.getLabels().clear();

		CellRange init = new CellRange(initialLattice);
		CellRange result = new CellRange(resultLattice);

		hk.compute(init, result);

		hk.relabel(result);

		isComputed = true;
	}

	public void test()
	{
		int north, east, west, south;
		int rows = resultLattice.length, cols = resultLattice[0].length;
		int current;
		for(int i = 0 ; i < rows ; i++){
			for(int j = 0 ; j < cols ; j++){
				current = resultLattice[i][j].getValue();
				if(current != 0)
				{
					north = (i == 0) ? 0 : resultLattice[i - 1][j].getValue();
					south = (i == (rows - 1)) ? 0 : resultLattice[i + 1][j].getValue();
					west = (j == 0) ? 0 : resultLattice[i][j - 1].getValue();
					east = (j == (cols - 1)) ? 0 : resultLattice[i][j + 1].getValue();

					assert (north == 0 || north == current);
					assert (east == 0 || east == current);
					assert (west == 0 || west == current);
					assert (south == 0 || south == current);
				}
			}
		}
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		Cell[][] cells;

		final String endLine = System.lineSeparator();

		if(!isComputed)
		{
			cells = initialLattice;
			buf.append("The sizes of initial lattice is ");
		}
		else
		{
			cells = resultLattice;
			buf.append("The sizes of result lattice is ");
		}

		buf.append(cells.length).append("x").append(cells[0].length)
				.append(".").append(endLine);

		buf.append(toPrintableLattice(cells));

		return buf.toString();
	}

	private String toPrintableLattice(Cell[][] cells)
	{
		StringBuilder buf = new StringBuilder();
		for(Cell[] cell : cells)
		{
			buf.append(cell[0].getValue());
			for(int j = 1 ; j < cells[0].length ; j++)
			{
				buf.append(",").append(cell[j]);
			}
			buf.append(System.lineSeparator());
		}
		return buf.toString();
	}

	public void generateNewLattice(double[][] array, double chance)
	{
		if(initialLattice == null)
		{
			initialLattice = new Cell[array.length][array[0].length];
			resultLattice = new Cell[array.length][array[0].length];
			for(int i = 0 ; i < initialLattice.length ; i++)
			{
				for(int j = 0 ; j < initialLattice[i].length ; j++)
				{
					initialLattice[i][j] = new Cell();
					resultLattice[i][j] = new Cell();
				}
			}
		}

		for(int i = 0 ; i < array.length ; i++)
		{
			for(int j = 0 ; j < array[0].length ; j++)
			{
				initialLattice[i][j].setValue((array[i][j] < chance) ? 1 : 0);
			}
		}

		isComputed = false;
	}

	public TwoDimensionalPercolation checkEdges()
	{
		return TwoDimensionalPercolation.checkEdges(resultLattice);
	}
}
