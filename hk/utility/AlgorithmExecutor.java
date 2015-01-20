package hk.utility;

import hk.*;
import hk.cell.*;

import java.util.*;
import java.util.concurrent.*;

public class AlgorithmExecutor
{
	private int countOfThreads;
	private Future<?>[] barrier;
	private ExecutorService pool;
	private CellRange<Integer>[] init;
	private CellRange<Integer>[] result;
	private Cell<Integer>[][] initialLattice;
	private Cell<Integer>[][] resultLattice;

	public AlgorithmExecutor(Cell<Integer>[][] initialLattice,
         Cell<Integer>[][] resultLattice, int countOfThreads)
	{
		// First bind required fields
		this.countOfThreads = countOfThreads;
		this.initialLattice = initialLattice;
		this.resultLattice = resultLattice;

		// Fill array for further range distribution through threads
		int colonForThread[] = generateBounds(this.countOfThreads);

		// Initialise the thread pool, ranges and barrier
		pool = Executors.newFixedThreadPool(this.countOfThreads);
		init = new CellRange[this.countOfThreads];
		result = new CellRange[this.countOfThreads];
		barrier = new Future[this.countOfThreads];

		int start = 0, end = 0;

		// Insert tasks into pool
		for(int i = 0 ; i < this.countOfThreads ; ++i){
			end += colonForThread[i];
			init[i] = new CellRange<>(this.initialLattice, start, 0,
					end, this.initialLattice[0].length);
			result[i] = new CellRange<>(this.resultLattice, start, 0,
					end, this.initialLattice[0].length);
			start = end;
		}
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

	public boolean runMarkers()
	{
		IntegerCellMarker[] markers = new IntegerCellMarker[countOfThreads];
		for(int i = 0 ; i < markers.length ; i++){
			markers[i] = new IntegerCellMarker(new IntegerUnionFindHelper(), init[i], result[i]);
		}

		runTask(markers);

		return waitTask(markers);
	}

	public boolean runCorrectors()
	{
		BoundaryIntegersCorrector[] correctors = new BoundaryIntegersCorrector[countOfThreads - 1];
		for(int i = 1 ; i < countOfThreads ; ++i)
		{
			correctors[i - 1] = new BoundaryIntegersCorrector(result[i]);
		}

		// Merge the bound labels
		runTask(correctors);

		return waitTask(correctors);
	}

	public boolean createResultLattice()
	{
		IntegerLatticeCreator[] creators = new IntegerLatticeCreator[countOfThreads];
		for(int i = 0 ; i < countOfThreads ; i++){
			creators[i] = new IntegerLatticeCreator(result[i]);
		}

		runTask(creators);

		return waitTask(creators);
	}

	private void runTask(Runnable[] tasks)
	{
		for(int i = 0 ; i < tasks.length ; ++i){
			barrier[i] = pool.submit(tasks[i]);
		}
	}

	private boolean waitTask(Runnable[] tasks)
	{
		try{
			for(int i = 0 ; i < tasks.length ; ++i){
				barrier[i].get();
			}
		}
		catch(InterruptedException | ExecutionException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void freeResources()
	{
		pool.shutdown();
		while(!pool.isTerminated()){}
	}
}
