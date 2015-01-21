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
	private static ConcurrentSkipListSet<Pair<Integer, Integer>> unitedLabels =
			new ConcurrentSkipListSet<>();
	private static ConcurrentSkipListSet<Pair<Integer, Integer>> boundariesLabels =
			new ConcurrentSkipListSet<>();
	private static ConcurrentMap<Integer, Pair<Integer, Integer>> globalLabels =
			new ConcurrentSkipListMap<>();
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
		// Get the list of label intersection.
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

	public static void insertBoundaryLabel(int x, int y)
	{
		boundariesLabels.add(new Pair<>(x, y));
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
		Integer x, y, zero = new Integer(0);

		for(Pair<Integer, Integer> coordinates : boundariesLabels)
		{
			x = coordinates.getFirst();
			y = coordinates.getLast();
			if(x.compareTo(zero) + y.compareTo(zero) == 2)
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
	private static List<Integer[]> getLabelIntersections()
	{
		// If unions are absent, return empty list
		Pair<Integer, Integer>[] it = unitedLabels.toArray(new Pair[unitedLabels.size()]);
		if(it.length == 0)
		{
			return new ArrayList<>();
		}

		// Add first pair
		List<HashSet<Integer>> result = new ArrayList<>();
		Pair<Integer, Integer> pair = it[0];
		result.add(new HashSet<Integer>());
		Collections.addAll(result.get(0), pair.getFirst(), pair.getLast());

		// Add other pairs
		int found;
		TreeSet<Integer> indexes = new TreeSet<>();
		HashSet<Integer> set;
		for(int j = 1 ; j < it.length ; ++j)
		{
			found = 0;
			indexes.clear();
			pair = it[j];

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
					result.add(new HashSet<Integer>());
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

		List<Integer[]> list = new ArrayList<>();
		for(HashSet<Integer> value : result)
		{
			list.add(new TreeSet<>(value).toArray(new Integer[value.size()]));
		}

		return list;
	}

	public static void clear()
	{
		nextLatticeLabel.set(0);
		unitedLabels.clear();
		boundariesLabels.clear();
		globalLabels.clear();
	}
}
