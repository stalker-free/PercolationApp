package hk.utility;

import hk.cell.*;
import hk.CellRange;

import java.util.concurrent.*;

/**
 * Class for setting raw result lattice.
 */
public class IntegerCellMarker implements Runnable, Callable<Void>
{
	private IntegerUnionFindHelper uf;
	private CellRange<Integer> init, result;
	private static final Cell<Integer> zero = new IntegerCell(0);

	public IntegerCellMarker(IntegerUnionFindHelper uf, CellRange<Integer> init,
          CellRange<Integer> result)
	{
		this.uf = uf;
		this.init = init;
		this.result = result;
	}

	@Override
	public void run()
	{
		Cell<Integer> north, west, readOnly;
		Integer northValue, westValue;

		for(CellRange<Integer>.CellIterator it =
		    (CellRange<Integer>.CellIterator)init.iterator(),
		    resultIt = (CellRange<Integer>.CellIterator)result.iterator();
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

			north = it.getNorth(zero);
			west = it.getWest(zero);
			northValue = north.getValue();
			westValue = west.getValue();

			// Determine cell's label by surrounding cells
			if(northValue == 0 && westValue == 0)
			{
				// Mark lone cell as element of new cluster
				uf.makeNewCluster(resultIt);

			}
			else if(northValue == 1 && westValue == 1)
			{
				// Get nearest reference to cluster
				// centers of each neighbour cells
				north = resultIt.getReference(resultIt.getNorth(zero));
				west = resultIt.getReference(resultIt.getWest(zero));

				if(north.isReference() && west.isReference())
				{
					// Both cell are references
					uf.union(resultIt.get((ReferenceCell<Integer>)north),
							resultIt.get((ReferenceCell<Integer>)west));
				}
				else if(north.isReference())
				{
					// North cell is reference
					uf.union(resultIt.get((ReferenceCell<Integer>)north), west);
				}
				else if(west.isReference())
				{
					// West cell is reference
					uf.union(north, resultIt.get((ReferenceCell<Integer>)west));
				}
				else
				{
					// Both cell contains value
					uf.union(north, west);
				}

				resultIt.set(resultIt.getWestReference());
			}
			else if(northValue == 1)
			{
				resultIt.set(resultIt.getNorthReference());
			}
			else
			{
				resultIt.set(resultIt.getWestReference());
				// Check the bound of range
				if(it.getNorth(zero, 0).getValue() != 0)
				{
					IntegerUnionFindHelper.insertBoundaryLabel(resultIt.getCurrentX(),
							resultIt.getCurrentY());
				}
			}
		}
	}

	@Override
	public Void call() throws Exception{
		run();
		return null;
	}
}
