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
	private Map<Integer, Integer> sizes;
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

	private int[] generateBounds(int areasCount)
	{
		int bounds[] = new int[areasCount];

		int remaining = initialLattice.length % countOfThreads;
		int quotient = (initialLattice.length - remaining) / countOfThreads;

		Arrays.fill(bounds, quotient);
		for(int i = 0 ; remaining > 0 ; ++i, --remaining)
		{
			++bounds[i];
		}

		return bounds;
	}

	private void compute()
	{
		// Don't create thread pool if only one is needed
		if(countOfThreads == 1)
		{
			new CellMarker(new UnionFindHelper(), new CellRange(initialLattice),
					new CellRange(resultLattice)).run();
			return;
		}

		// Fill array for further range distribution through threads
		int colonForThread[] = generateBounds(countOfThreads);

		// Initialise the thread pool
		int start = 0, end = 0, i = 0;

		CellRange init;
		CellRange result;
		CellMarker[] markers = new CellMarker[countOfThreads];

		// Insert tasks into pool
		do{
			end += colonForThread[i];
			init = new CellRange(initialLattice, start, 0, end, initialLattice[0].length);
			result = new CellRange(resultLattice, start, 0, end, initialLattice[0].length);
			markers[i] = new CellMarker(new UnionFindHelper(), init, result);
			start = end;
		}while(++i < countOfThreads);

		Thread[] pool = new Thread[countOfThreads];
		// Run tasks
		for(i = 0 ; i < countOfThreads ; ++i)
		{
			pool[i] = new Thread(markers[i]);
			pool[i].start();
		}

		// Wait all tasks and free resources
		try
		{
			for(i = 0 ; i < countOfThreads ; ++i)
			{
				pool[i].join();
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void clusterize()
	{
		resultLattice = new Cell[initialLattice.length][initialLattice[0].length];

		Calendar calendar = Calendar.getInstance();
		timeElapsed = -calendar.getTimeInMillis();

		compute();

		calendar = Calendar.getInstance();
		timeElapsed += calendar.getTimeInMillis();

		if(countOfThreads > 1)
		{
			LatticeMerger.mergeLabels(resultLattice, generateBounds(countOfThreads));
		}

		sizes = new HashMap<>();
		int val;
		for(Cell[] latticeRow : resultLattice)
		{
			for(Cell elem : latticeRow)
			{
				val = elem.getValue();
				if(val == 0) continue;
				if(sizes.get(val) == null)
				{
					sizes.put(val, 1);
				}
				else
				{
					sizes.put(val, sizes.get(val) + 1);
				}
			}
		}
	}

	public boolean test()
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

					if (!(north == 0 || north == current)) return false;
					if (!(east == 0 || east == current)) return false;
					if (!(west == 0 || west == current)) return false;
					if (!(south == 0 || south == current)) return false;
				}
			}
		}
		return true;
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

		if(resultLattice != null && sizes != null)
		{
			int i = 1;
			buf.append("Size of clusters:").append(endLine);
			for(Map.Entry<Integer, Integer> size : sizes.entrySet())
			{
				buf.append(size.getKey()).append(": ").append(size.getValue()).append(endLine);
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