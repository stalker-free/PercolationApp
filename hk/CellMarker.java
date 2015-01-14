package hk;

public class CellMarker implements Runnable
{
	private UnionFindHelper uf;
	private CellRange init, result;

	public CellMarker(UnionFindHelper uf, CellRange init, CellRange result)
	{
		this.uf = uf;
		this.init = init;
		this.result = result;
	}

	@Override
	public void run()
	{
		Cell up, left, readOnly, north, west;
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
				north = resultIt.getNorth();
				west = resultIt.getWest();
				resultIt.set(new IntegerCell(Math.max(north.getValue(), west.getValue())));
			}
			else
			{
				north = resultIt.getNorth();
				west = resultIt.getWest();			
				resultIt.set(new IntegerCell(uf.union(north, west)));
			}
		}

		uf.relabel(result);
	}
}
