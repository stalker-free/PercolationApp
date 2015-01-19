package hk;

import java.util.*;

/**
 * Class, which provides methods for Hoshen-Kopelman algorithm.
 */
public class IntegerUnionFindHelper
{
	private Map<Integer, Integer> labels = new HashMap<>();
	private static Set<Pair> unitedLabels = new LinkedHashSet<>();
	private static Map<Integer, Pair> globalLabels = new HashMap<>();
	private static int nextLatticeLabel;

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
		unitedLabels.add(new Pair(min.getValue(), max.getValue()));

		return min.getValue();
	}

	/**
	 * Create new cluster label.
	 */
	public void makeNewCluster(CellRange<Integer>.CellIterator iterator)
	{
		int valueToAdd = nextLabel();
		labels.put(valueToAdd, valueToAdd);
		valueToAdd += 1;
		globalLabels.put(valueToAdd, new Pair(iterator.getCurrentX(), iterator.getCurrentY()));
		iterator.set(new IntegerCell(valueToAdd));
	}

	/**
	 * Remove redundant cluster labels.
	 */
	public static void relabel(Cell<Integer>[][] lattice)
	{
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

	/**
	 * Returns a list which contains label intersection.
	 * @return list containing label intersection.
	 */
	private static List<TreeSet<Integer>> getLabelIntersections()
	{
		List<TreeSet<Integer>> result = new ArrayList<>();
		TreeSet<Integer> indexes = new TreeSet<>();

		// Add first pair
		Iterator<Pair> it = unitedLabels.iterator();
		if(!it.hasNext()) return result;
		Pair pair = it.next();
		result.add(new TreeSet<Integer>());
		Collections.addAll(result.get(0), pair.getFirst(), pair.getLast());

		// Add other pairs
		int found;
		for(; it.hasNext() ; )
		{
			found = 0;
			indexes.clear();
			pair = it.next();

			// Find already inserted labels
			for(int i = 0 ; i < result.size() ; ++i)
			{
				if(result.get(i).contains(pair.getFirst()))
				{
					indexes.add(i);
					++found;
					if(found > 1) break;
				}

				if(result.get(i).contains(pair.getLast()))
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
		nextLatticeLabel = 0;
		unitedLabels.clear();
		globalLabels.clear();
	}
}
