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
	/**
	 * Local labels of current range.
	 */
	private Map<Integer, Integer> labels = new HashMap<>();

	/**
	 * Interfering cluster's labels.
	 */
	private static ConcurrentSkipListSet<Pair<Integer, Integer>> unitedLabels =
			new ConcurrentSkipListSet<>();

	/**
	 * Interfering boundary cluster's labels.
	 */
	private static ConcurrentSkipListSet<Pair<Integer, Integer>> boundariesLabels =
			new ConcurrentSkipListSet<>();

	/**
	 * Global labels of whole lattice.
	 */
	private static ConcurrentMap<Integer, Pair<Integer, Integer>> globalLabels =
			new ConcurrentSkipListMap<>();

	/**
	 * Global lattice labels' counter.
	 */
	private static AtomicInteger nextLatticeLabel = new AtomicInteger();

	public IntegerUnionFindHelper(){}

	/**
	 * Search for real cluster label.
	 * @param cell cell for label searching.
	 * @return Point to real cluster label of the cell.
	 */
	public Integer find(Cell<Integer> cell)
	{
		Integer index = cell.getValue();
		if(index > 0) index -= 1;
		while(!index.equals(labels.get(index)))
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

		Integer result = find(min);
		labels.put(result, find(max));
		uniteLabels(min.getValue(), max.getValue());

		return min.getValue();
	}

	/**
	 * Create new cluster label.
	 */
	public int makeNewCluster(CellRange<Integer>.CellIterator iterator)
	{
		Integer valueToAdd = nextLabel();
		labels.put(valueToAdd, valueToAdd);
		valueToAdd += 1;

		globalLabels.put(valueToAdd, new Pair<>(iterator.getCurrentX(), iterator.getCurrentY()));

		iterator.set(new IntegerCell(valueToAdd));
		return valueToAdd;
	}

	/**
	 * Remove redundant cluster labels.
	 */
	public static void relabel(Cell<Integer>[][] lattice)
	{
		mergeBoundaryLabels(lattice);

		// Get the list of label intersection
		List<Integer[]> labelsOfSameCluster = getLabelIntersections();
		Set<Map.Entry<Integer, Pair<Integer, Integer>>> entrySet = globalLabels.entrySet();
		Pair<Integer, Integer> pair;

		// Relabel every center of the cluster
		for(Integer[] interferedLabels : labelsOfSameCluster)
		{
			// Search for interfering labels
			for(Map.Entry<Integer, Pair<Integer, Integer>> clusterCenter : entrySet)
			{
				if(Arrays.binarySearch(interferedLabels, clusterCenter.getKey()) > 0)
				{
					// Set the least label to the cell
					pair = clusterCenter.getValue();
					lattice[pair.getFirst()][pair.getLast()].setValue(interferedLabels[0]);
				}
			}
		}
	}

	/**
	 * Inserts the label pair to unite to the container of unions.
	 */
	public static void uniteLabels(Integer first, Integer second)
	{
		if(first.compareTo(second) == 1)
		{
			unitedLabels.add(new Pair<>(second, first));
		}
		else
		{
			unitedLabels.add(new Pair<>(first, second));
		}
	}

	/**
	 * Inserts the label pair to unite to the container of boundaries.
	 */
	public static void insertBoundaryLabel(int x, int y)
	{
		boundariesLabels.add(new Pair<>(x, y));
	}

	/**
	 * Receive and increase the label counter.
	 * @return Previous value.
	 */
	private static int nextLabel()
	{
		return nextLatticeLabel.getAndIncrement();
	}

	/**
	 * Merge container of boundaries and container of unions.
	 * @param lattice input lattice with references.
	 */
	private static void mergeBoundaryLabels(Cell<Integer>[][] lattice)
	{
		Cell<Integer> north, west;
		Integer x, y, zero = new Integer(0);

		for(Pair<Integer, Integer> coordinates : boundariesLabels)
		{
			x = coordinates.getFirst();
			y = coordinates.getLast();

			// Check for positive coordinates
			if(x.compareTo(zero) + y.compareTo(zero) == 2)
			{
				// Insert labels
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
	private static List<Integer[]> getLabelIntersections()
	{
		// If unions are absent, return empty list
		Pair<Integer, Integer>[] it = unitedLabels.toArray(new Pair[unitedLabels.size()]);
		if(it.length == 0)
		{
			return new ArrayList<>();
		}

		// Add first pair
		List<HashSet<Integer>> list = new ArrayList<>();
		Pair<Integer, Integer> pair = it[0];
		list.add(new HashSet<Integer>());
		Collections.addAll(list.get(0), pair.getFirst(), pair.getLast());

		// Add other pairs
		int found;
		TreeSet<Integer> indexSet = new TreeSet<>();
		HashSet<Integer> set;
		for(int j = 1 ; j < it.length ; ++j)
		{
			found = 0;
			indexSet.clear();
			pair = it[j];

			// Find already inserted labels
			for(int i = 0 ; i < list.size() ; ++i)
			{
				set = list.get(i);
				if(set.contains(pair.getFirst()))
				{
					indexSet.add(i);
					++found;
					if(found > 1) break;
				}

				if(set.contains(pair.getLast()))
				{
					indexSet.add(i);
					++found;
					if(found > 1) break;
				}
			}

			// Perform the action
			switch(indexSet.size())
			{
				// Add new interference pair
				case 0:
					list.add(new HashSet<Integer>());
					Collections.addAll(list.get(list.size() - 1), pair.getFirst(), pair.getLast());
					break;
				// Add new interference to already existing
				case 1:
					Collections.addAll(list.get(indexSet.first()), pair.getFirst(), pair.getLast());
					break;
				// Merge interferences
				case 2:
					list.get(indexSet.first()).addAll(list.get(indexSet.last()));
					list.remove((int)indexSet.last());
					break;
			}
		}

		// Optimise the container to return
		List<Integer[]> result = new ArrayList<>();
		for(HashSet<Integer> value : list)
		{
			result.add(new TreeSet<>(value).toArray(new Integer[value.size()]));
		}

		return result;
	}

	/**
	 * Clear static fields.
	 */
	public static void clear()
	{
		nextLatticeLabel.set(0);
		unitedLabels.clear();
		boundariesLabels.clear();
		globalLabels.clear();
	}
}
