package hk;

public class IntegerCellMarker implements Runnable
{
	private IntegerUnionFindHelper uf;
	private CellRange<Integer> init, result;

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
		Cell<Integer> north, west, readOnly, zero = new IntegerCell(0);
		int northValue, westValue;

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
				north = resultIt.getReference(resultIt.getNorth(zero));
				west = resultIt.getReference(resultIt.getWest(zero));

				if(north.getValue() == null && west.getValue() == null)
				{
					uf.union(resultIt.get((ReferenceCell<Integer>)north),
							resultIt.get((ReferenceCell<Integer>)west));
				}
				else if(north.getValue() == null)
				{
					uf.union(resultIt.get((ReferenceCell<Integer>)north), west);
				}
				else if(west.getValue() == null)
				{
					uf.union(north, resultIt.get((ReferenceCell<Integer>)west));
				}
				else
				{
					uf.union(north, west);
				}

				// TODO: Remember to merge clusters...
				resultIt.set(resultIt.getWestReference());
			}
			else if(northValue == 1)
			{
				resultIt.set(resultIt.getNorthReference());
			}
			else
			{
				resultIt.set(resultIt.getWestReference());
			}
		}
	}
}