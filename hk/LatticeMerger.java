package hk;

import java.util.*;

public class LatticeMerger
{
	private static boolean insert(Map<Integer, Set<Integer>> map, Integer key, Integer value)
	{
		if(!map.containsKey(key))
		{
			map.put(key, new HashSet<Integer>());
		}
		return map.get(key).add(value);
	}

	private static int get(Map<Integer, Set<Integer>> map, Integer key)
	{
		return Collections.min(map.get(key));
	}

	private static List<Map<Set<Integer>, Integer>> reverse(List<Map<Integer, Set<Integer>>> list)
	{
		List<Map<Set<Integer>, Integer>> result = new ArrayList<>();

		for(Map<Integer, Set<Integer>> map: list)
		{
			result.add(new HashMap<Set<Integer>, Integer>());
			for(Map.Entry<Integer, Set<Integer>> entry : map.entrySet())
			{
				result.get(result.size() - 1).put(entry.getValue(), entry.getKey());
			}
		}

		return result;
	}

	public static void mergeLabels(Cell[][] lattice, int[] bounds){
		int bound = 0, countOfBounds = Math.min(lattice.length, bounds.length);
		Cell first, second;
		CellRange firstRange, secondRange;
		CellRange.CellIterator firstIt, secondIt;
		List<Map<Integer, Set<Integer>>> references = new LinkedList<>();

		// Generate all reference labels
		for(int i = 0 ; i < (countOfBounds - 1) ; i++){
			references.add(new HashMap<Integer, Set<Integer>>());

			bound += bounds[i];
			firstRange = new CellRange(lattice, bound - 1, 0, bound, lattice[0].length);
			secondRange = new CellRange(lattice, bound, 0, bound + 1, lattice[0].length);

			firstIt = (CellRange.CellIterator)firstRange.iterator();
			secondIt = (CellRange.CellIterator)secondRange.iterator();

			// Iterate over bounds
			while(firstIt.hasNext()){
				first = firstIt.next();
				second = secondIt.next();

				if(first.getValue() == 0 || second.getValue() == 0){
					continue;
				}

				// Write reference label
				insert(references.get(i), second.getValue(), first.getValue());
			}
		}

		// Relabel first chunk
		{
			List<Map<Set<Integer>, Integer>> reversedList = reverse(references);
			bound = bounds[0];
			firstRange = new CellRange(lattice, 0, 0, bound, lattice[0].length);
			firstIt = (CellRange.CellIterator)firstRange.iterator();
			while(firstIt.hasNext()){
				first = firstIt.next();
				if(first.getValue() == 0) continue;
				for(Map.Entry<Set<Integer>, Integer> entry : reversedList.get(0).entrySet()){
					// [2, 3, 4] -> 1
					if(entry.getKey().contains(first.getValue())){
						first.setValue(Collections.min(entry.getKey()));
					}
				}
			}
		}

		// Relabel whole lattice
		int value;
		for(int i = 1 ; i < countOfBounds ; i++){
			firstRange = new CellRange(lattice,
				bound, 0, (bound += bounds[i]), lattice[0].length);
			firstIt = (CellRange.CellIterator)firstRange.iterator();
			while(firstIt.hasNext())
			{
				first = firstIt.next();
				if(first.getValue() == 0) continue;
				if(references.get(i - 1).containsKey(first.getValue())){
					first.setValue(get(references.get(i - 1), first.getValue()));
				}
			}
		}
	}
}
