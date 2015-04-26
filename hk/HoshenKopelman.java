package hk;

import hk.cell.*;

import java.util.*;

/**
 * Class, which provide methods for Hoshen-Kopelman algorithm.
 */
public class HoshenKopelman
{
	private final List<Integer> labels = new ArrayList<>();

	/**
	 * Search for real cluster label.
	 * @param cell cell for label searching.
	 * @return Point to real cluster label of the cell.
	 */
	public int find(Cell cell)
	{
		int index = cell.getValue();
		if(index > 0) index -= 1;
		while(index != labels.get(index))
		{
			labels.set(index, labels.get(labels.get(index)));
			index = labels.get(index);
		}
		return index;
	}

	/**
	 * Merge two neighbors clusters into one.
	 * @param first,second cell of clusters.
	 * @return Minimal label of resulting cluster.
	 */
	public int union(Cell first, Cell second)
	{
		Cell max, min;
		if(first.getValue() < second .getValue())
		{
			min = first;
			max = second;
		}
		else
		{
			min = second;
			max = first;
		}
		int result = find(min);
		labels.set(result, find(max));
		return min.getValue();
	}

	/**
	 * Create new cluster label.
	 * @return New label.
	 */
	public int makeNewCluster()
	{
		labels.add(labels.size());
		return labels.size();
	}

	public void compute(CellRange init, CellRange result)
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
				resultIt.set(new IntegerCell(makeNewCluster()));
			}
			else if(upValue == 0 || leftValue == 0)
			{
				resultIt.set(new IntegerCell(Math.max(resultIt.getNorth().getValue(),
						resultIt.getWest().getValue())));
			}
			else
			{
				resultIt.set(new IntegerCell(union(resultIt.getNorth(), resultIt.getWest())));
			}
		}
	}

	/**
	 * Remove redundant cluster labels.
	 * @param dataset lattice slice.
	 * @return Count of new labels.
	 */
	public int relabel(CellRange dataset)
	{
		Map<Integer, Integer> labelSet = new HashMap<>();
		int found;
		Cell cell;

		CellRange.CellIterator it = (CellRange.CellIterator)dataset.iterator();
		while(it.hasNext())
		{
			cell = it.next();
			if(cell.getValue() == 0) continue;

			found = find(cell);
			if(!labelSet.containsKey(found))
			{
				labelSet.put(found, 1 + labelSet.size());
			}

			cell.setValue(labelSet.get(found));
		}

		return labelSet.size();
	}

	public List<Integer> getLabels()
	{
		return labels;
	}
}
