package hk;

import java.util.*;

/**
 * Class, which provide methods for Hoshen-Kopelman algorithm.
 */
public class UnionFindHelper
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
