package hk;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class, which provides methods for Hoshen-Kopelman algorithm.
 */
public class IntegerUnionFindHelper
{
	private Map<Integer, Integer> labels = new HashMap<>();
	private static int nextLatticeLabel;

	public IntegerUnionFindHelper()
	{
		nextLatticeLabel = 0;
	}

	/**
	 * Search for real cluster label.
	 * @param cell cell for label searching.
	 * @return Point to real cluster label of the cell.
	 */
	public int find(Cell<Integer> cell)
	{
		int index = cell.getValue();
		if(index > 0) index -= 1;
		while(index != labels.get(index))
		{
			labels.put(index, labels.get(labels.get(index)));
			index = labels.get(index);
		}
		return index;
	}

	/**
	 * Merge two neighbors clusters into one.
	 * @param first,second cell of clusters.
	 * @return Minimal label of resulting cluster.
	 */
	public int union(Cell<Integer> first, Cell<Integer> second)
	{
		Cell max, min;
		if(first.getValue() < second.getValue())
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
		labels.put(result, find(max));
		return (int)min.getValue();
	}

	/**
	 * Create new cluster label.
	 * @return New label.
	 */
	public int makeNewCluster()
	{
		int valueToAdd = nextLabel();
		labels.put(valueToAdd, valueToAdd);
		return valueToAdd + 1;
	}

	/**
	 * Remove redundant cluster labels.
	 * @param dataset lattice slice.
	 * @return Count of new labels.
	 */
	public int relabel(CellRange<Integer> dataset)
	{
		Map<Integer, Integer> labelSet = new HashMap<>();
		int found;
		Cell<Integer> cell;

		CellRange<Integer>.CellIterator it = (CellRange<Integer>.CellIterator)dataset.iterator();
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

	private static synchronized int nextLabel()
	{
		return nextLatticeLabel++;
	}

	public HashMap<Integer, Integer> getLabels()
	{
		return new HashMap<>(labels);
	}

	public void setLabels(Map<Integer, Integer> labels){
		this.labels = new HashMap<>(labels);
	}
}
