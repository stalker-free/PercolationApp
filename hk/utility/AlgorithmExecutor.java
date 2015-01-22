package hk.utility;

import hk.*;
import hk.cell.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class for running various parallel tasks.
 */
public class AlgorithmExecutor
{
	private int countOfThreads;
	private ExecutorService pool;
	private CellRange<Integer>[] init;
	private CellRange<Integer>[] result;
	private Cell<Integer>[][] initialLattice;

	/**
	 * Constructs this class to perform parallel task operating.
	 * @param initialLattice input lattice.
	 * @param resultLattice output lattice.
	 * @param countOfThreads the count of threads to create.
	 */
	public AlgorithmExecutor(Cell<Integer>[][] initialLattice,
         Cell<Integer>[][] resultLattice, int countOfThreads)
	{
		if(countOfThreads < 1) throw new IllegalArgumentException();
		// First bind required fields
		this.countOfThreads = countOfThreads;
		this.initialLattice = initialLattice;

		// Fill array for further range distribution through threads
		int colonForThread[] = generateBounds(this.countOfThreads);

		// Initialise the thread pool, ranges and barrier
		pool = Executors.newFixedThreadPool(this.countOfThreads);
		init = new CellRange[this.countOfThreads];
		result = new CellRange[this.countOfThreads];

		int start = 0, end = 0;

		// Insert tasks into pool
		for(int i = 0 ; i < this.countOfThreads ; ++i){
			end += colonForThread[i];
			init[i] = new CellRange<>(this.initialLattice, start, 0,
					end, this.initialLattice[0].length);
			result[i] = new CellRange<>(resultLattice, start, 0,
					end, resultLattice[0].length);
			start = end;
		}
	}

	/**
	 * Creates an array to distribute uniformly tasks through threads.
	 * @param areasCount the count of threads for next execution.
	 * @return Array containing uniformly distributed integers.
	 */
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

	/**
	 * Start cell marker's task.
	 */
	public void runMarkers()
	{
		List<Callable<Void>> markers = new ArrayList<>(countOfThreads);

		for(int i = 0 ; i < countOfThreads ; i++){
			markers.add(new IntegerCellMarker(new IntegerUnionFindHelper(), init[i], result[i]));
		}

		runTask(markers);
	}

	/**
	 * Start boundaries' corrector's task.
	 */
	public void runCorrectors()
	{
		if(countOfThreads < 2) return;
		List<Callable<Void>> correctors = new ArrayList<>(countOfThreads - 1);
		for(int i = 1 ; i < countOfThreads ; ++i)
		{
			correctors.add(new BoundaryIntegersCorrector(result[i]));
		}

		runTask(correctors);
	}

	/**
	 * Start result lattice creator's task.
	 */
	public void createResultLattice()
	{
		List<Callable<Void>> creators = new ArrayList<>(countOfThreads);
		for(int i = 0 ; i < countOfThreads ; i++){
			creators.add(new IntegerLatticeCreator(result[i]));
		}

		runTask(creators);
	}

	/**
	 * Execute task on distributed ranges.
	 * @param tasks callable object to execute.
	 */
	private void runTask(List<Callable<Void>> tasks)
	{
		try{
			pool.invokeAll(tasks);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	/**
	 * Shutdowns the threads pool.
	 * After that the executor can't be used anymore.
	 */
	public void freeResources()
	{
		pool.shutdown();
		while(!pool.isTerminated()){}
	}

	public int getCountOfThreads(){
		return countOfThreads;
	}
}
