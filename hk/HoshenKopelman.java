package hk;

import java.util.*;

/**
 * This class is created for wrapping lattice
 * and executing the Hoshen-Kopelman algorithm on it.
 */
public class HoshenKopelman
{
	private Cell[][] initialLattice;
	private Cell[][] resultLattice;
	private UnionFindHelper uf;
	private int[] sizes;
	private int countOfThreads;
	private long timeElapsed;

	public HoshenKopelman(Cell[][] lattice){
		this(lattice, 1);
	}

	public HoshenKopelman(Cell[][] lattice, int countOfThreads)
	{
		if(countOfThreads < 1)
		{
			throw new IllegalArgumentException("Object must have at least one thread.");
		}

		initialLattice = new Cell[lattice.length][];

		for(int i = 0 ; i < initialLattice.length ; i++)
		{
			initialLattice[i] = Arrays.copyOf(lattice[i], lattice[i].length);
		}

		this.countOfThreads = countOfThreads;
	}

	private void compute(CellRange init, CellRange result)
	{
		Cell up, left, readOnly;
		int upValue, leftValue;

		for(CellRange.CellIterator it =
		    (CellRange.CellIterator)init.iterator(),
		    resultIt = (CellRange.CellIterator)result.iterator();
		    it.hasNext() ;)
		{
			// Get next cells
			readOnly = it.next();
			resultIt.next();

			// If cell = zero then just skip it
			if(readOnly.getValue() == 0)
			{
				resultIt.set(new IntegerCell(0));
				continue;
			}

			up = it.getNorth();
			left = it.getWest();
			upValue = up.getValue();
			leftValue = left.getValue();

			// Determine cell's label by surrounding cells
			if(upValue == 0 && leftValue == 0)
			{
				// Mark lone cell as element of new cluster
				resultIt.set(new IntegerCell(uf.makeNewCluster()));
			}
			else if(upValue == 0 || leftValue == 0)
			{
				resultIt.set(new IntegerCell(Math.max(resultIt.getNorth().getValue(),
						resultIt.getWest().getValue())));
			}
			else
			{
				resultIt.set(new IntegerCell(uf.union(resultIt.getNorth(), resultIt.getWest())));
			}
		}
	}

	public void clusterize()
	{
		resultLattice = new Cell[initialLattice.length][initialLattice[0].length];
		uf = new UnionFindHelper();

		CellRange init = new CellRange(initialLattice);
		CellRange result = new CellRange(resultLattice);

		Calendar calendar = Calendar.getInstance();
		timeElapsed = -calendar.getTimeInMillis();
		compute(init, result);
		calendar = Calendar.getInstance();
		timeElapsed += calendar.getTimeInMillis();

		sizes = new int[uf.relabel(result)];

		int val;
		for(Cell[] latticeRow : resultLattice)
		{
			for(Cell elem : latticeRow)
			{
				val = elem.getValue();
				if(val > 0)
				{
					++sizes[val - 1];
				}
			}
		}
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
		StringBuffer buf = new StringBuffer();
		Cell[][] cells;

		final String endLine = System.lineSeparator();

		if(resultLattice == null)
		{
			cells = initialLattice;
			buf.append("The labels of initial lattice is ");
		}
		else
		{
			buf.append("Time spent for clusterizing: ").append(timeElapsed).
					append(" ms").append(endLine);
			cells = resultLattice;
			buf.append("The labels of result lattice is ");
		}

		buf.append(cells.length).append("x").append(cells[0].length)
				.append(".").append(endLine);

		buf.append(toPrintableLattice(cells));

		buf.append("Count of threads: ").append(countOfThreads)
				.append(".").append(endLine);

		if(resultLattice != null)
		{
			buf.append("Size of clusters:").append(endLine);
			for(int i = 0 ; i < sizes.length ; ++i)
			{
				buf.append(i + 1).append(": ").append(sizes[i]).append(endLine);
			}
		}

		return buf.toString();
	}

	public String getPrintableLattice()
	{
		return toPrintableLattice((resultLattice == null) ?
				initialLattice : resultLattice);
	}

	private String toPrintableLattice(Cell[][] cells)
	{
		StringBuffer buf = new StringBuffer();
		for(Cell[] cell : cells)
		{
			buf.append(cell[0].getValue());
			for(int j = 1 ; j < cells[0].length ; j++)
			{
				buf.append(",").append(cell[j].getValue());
			}
			buf.append(System.lineSeparator());
		}
		return buf.toString();
	}

	public int getCountOfThreads(){
		return countOfThreads;
	}

	public void setCountOfThreads(int countOfThreads){
		this.countOfThreads = countOfThreads;
	}
}
