package hk.utility;

import hk.cell.*;
import hk.CellRange;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Class, which provides methods for Hoshen-Kopelman algorithm.
 */
public class IntegerUnionFindHelper
{
	private Map<Integer, Integer> labels = new HashMap<>();
	private static ConcurrentSkipListSet<Pair> unitedLabels = new ConcurrentSkipListSet<>();
	private static ConcurrentSkipListSet<Pair> boundariesLabels = new ConcurrentSkipListSet<>();
	private static ConcurrentMap<Integer, Pair> globalLabels = new ConcurrentSkipListMap<>();
	private static AtomicInteger nextLatticeLabel = new AtomicInteger();

	public IntegerUnionFindHelper(){}

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
		Cell<Integer> max, min;
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
		uniteLabels(min.getValue(), max.getValue());

		return min.getValue();
	}

	/**
	 * Create new cluster label.
	 */
	public int makeNewCluster(CellRange.CellIterator iterator)
	{
		int valueToAdd = nextLabel();
		labels.put(valueToAdd, valueToAdd);
		valueToAdd += 1;
		globalLabels.put(valueToAdd, new Pair(iterator.getCurrentX(), iterator.getCurrentY()));
		iterator.set(new IntegerCell(valueToAdd));
		return valueToAdd;
	}

	/**
	 * Remove redundant cluster labels.
	 */
	public static void relabel(Cell<Integer>[][] lattice)
	{

		mergeBoundaryLabels(lattice);
		// Get the list of label intersection.
		List<TreeSet<Integer>> labelsOfSameCluster = getLabelIntersections();
		Pair pair;

		// Relabel every center of the cluster
		for(Map.Entry<Integer, Pair> clusterCenter : globalLabels.entrySet())
		{
			// Search for interfering labels
			for(TreeSet<Integer> interferedLabels : labelsOfSameCluster)
			{
				if(interferedLabels.contains(clusterCenter.getKey()))
				{
					// Set the least label to the cell
					pair = clusterCenter.getValue();
					lattice[pair.getFirst()][pair.getLast()].setValue(interferedLabels.first());
				}
			}
		}
	}

	public static void uniteLabels(int first, int second)
	{
		if(first > second)
		{
			unitedLabels.add(new Pair(second, first));
		}
		else
		{
			unitedLabels.add(new Pair(first, second));
		}
	}

	public static void insertBoundaryLabel(int x, int y)
	{
		boundariesLabels.add(new Pair(x, y));
	}

	private static int nextLabel()
	{
		return nextLatticeLabel.getAndIncrement();
	}

	public HashMap<Integer, Integer> getLabels()
	{
		return new HashMap<>(labels);
	}

	public void setLabels(Map<Integer, Integer> labels){
		this.labels = new HashMap<>(labels);
	}

	private static void mergeBoundaryLabels(Cell<Integer>[][] lattice)
	{
		Cell<Integer> north, west;
		int x, y;

		for(Pair coordinates : boundariesLabels)
		{
			x = coordinates.getFirst();
			y = coordinates.getLast();
			if(x > 0 && y > 0)
			{
				north = ReferenceCell.getCell(lattice, lattice[x - 1][y]);
				west = ReferenceCell.getCell(lattice, lattice[x][y - 1]);

				uniteLabels(north.getValue(), west.getValue());
			}
		}
	}

	/**
	 * Returns a list which contains label intersection.
	 * @return list containing label intersection.
	 */
	private static List<TreeSet<Integer>> getLabelIntersections()
	{
		// If unions are absent, return empty list
		Iterator<Pair> it = unitedLabels.iterator();
		if(!it.hasNext())
		{
			return new ArrayList<>();
		}

		// Add first pair
		List<TreeSet<Integer>> result = new ArrayList<>();
		Pair pair = it.next();
		result.add(new TreeSet<Integer>());
		Collections.addAll(result.get(0), pair.getFirst(), pair.getLast());

		// Add other pairs
		int found;
		TreeSet<Integer> indexes = new TreeSet<>();
		TreeSet<Integer> set;
		while(it.hasNext())
		{
			found = 0;
			indexes.clear();
			pair = it.next();

			// Find already inserted labels
			for(int i = 0 ; i < result.size() ; ++i)
			{
				set = result.get(i);
				if(set.contains(pair.getFirst()))
				{
					indexes.add(i);
					++found;
					if(found > 1) break;
				}

				if(set.contains(pair.getLast()))
				{
					indexes.add(i);
					++found;
					if(found > 1) break;
				}
			}

			// Perform the action
			switch(indexes.size())
			{
				// Add new interference pair
				case 0:
					result.add(new TreeSet<Integer>());
					Collections.addAll(result.get(result.size() - 1), pair.getFirst(), pair.getLast());
					break;
				// Add new interference to already existing
				case 1:
					Collections.addAll(result.get(indexes.first()), pair.getFirst(), pair.getLast());
					break;
				// Merge interferences
				case 2:
					result.get(indexes.first()).addAll(result.get(indexes.last()));
					result.remove((int)indexes.last());
					break;
			}
		}

		return result;
	}

	public static void clear()
	{
		nextLatticeLabel.set(0);
		unitedLabels.clear();
		boundariesLabels.clear();
		globalLabels.clear();
	}
}
