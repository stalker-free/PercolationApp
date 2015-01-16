package hk;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is created for wrapping lattice
 * and executing the Hoshen-Kopelman algorithm on it.
 */
public class HoshenKopelman
{
	private Cell<Integer>[][] initialLattice;
	private Cell<Integer>[][] resultLattice;
	private Map<Integer, Integer> sizes;
	private int countOfThreads;
	private long timeElapsed;

	public HoshenKopelman(Cell<Integer>[][] lattice){
		this(lattice, 1);
	}

	public HoshenKopelman(Cell<Integer>[][] lattice, int countOfThreads)
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
		Cell<Integer> zero = initialLattice[0][0].getZeroCell();
		// Don't create thread pool if only one is needed
		if(countOfThreads == 1)
		{
			new IntegerCellMarker(new IntegerUnionFindHelper(),
					new CellRange<>(initialLattice, zero),
					new CellRange<>(resultLattice, zero)).run();
			return;
		}

		// Fill array for further range distribution through threads
		int colonForThread[] = generateBounds(countOfThreads);

		// Initialise the thread pool
		ExecutorService pool = Executors.newFixedThreadPool(countOfThreads);
		int start = 0, end = 0, i = 0;

		CellRange<Integer> init;
		CellRange<Integer> result;
		IntegerCellMarker[] markers = new IntegerCellMarker[countOfThreads];

		// Insert tasks into pool
		do{
			end += colonForThread[i];
			init = new CellRange<>(initialLattice, start, 0, end, initialLattice[0].length, zero);
			result = new CellRange<>(resultLattice, start, 0, end, initialLattice[0].length, zero);
			markers[i] = new IntegerCellMarker(new IntegerUnionFindHelper(), init, result);
			start = end;
		}while(++i < countOfThreads);

		// Run tasks
		for(IntegerCellMarker marker : markers){
			pool.execute(marker);
		}

		// Wait all tasks and free resources
		pool.shutdown();
		while(!pool.isTerminated()){}
	}

	public void clusterize()
	{
		resultLattice = new Cell[initialLattice.length][initialLattice[0].length];

		Calendar calendar = Calendar.getInstance();
		timeElapsed = -calendar.getTimeInMillis();

		compute();

		calendar = Calendar.getInstance();
		timeElapsed += calendar.getTimeInMillis();

		sizes = new HashMap<>();
		int val;
		for(Cell<Integer>[] latticeRow : resultLattice)
		{
			for(Cell<Integer> elem : latticeRow)
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
