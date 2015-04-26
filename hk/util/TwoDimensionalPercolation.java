package hk.util;

import hk.CellRange;
import hk.cell.*;

import java.util.*;

public enum TwoDimensionalPercolation
{
	NONE,
	BY_X,
	BY_Y,
	BY_XY;

	public static TwoDimensionalPercolation checkEdges(Cell[][] lattice)
	{
		boolean percolationByX = isHaveIntersection(CellRange.getEdge(lattice, BY_X, true),
				CellRange.getEdge(lattice, BY_X, false));
		boolean percolationByY = isHaveIntersection(CellRange.getEdge(lattice, BY_Y, true),
				CellRange.getEdge(lattice, BY_Y, false));

		int result = (percolationByX ? 2 : 0) + (percolationByY ? 1 : 0);

		switch(result)
		{
			case 1:
				return BY_Y;

			case 2:
				return BY_X;

			case 3:
				return BY_XY;
		}

		return NONE;
	}

	public static boolean isHaveIntersection(CellRange first, CellRange second)
	{
		Set<Comparable> firstSet = new HashSet<>(), secondSet = new HashSet<>();
		for(CellRange.CellIterator firstIt = (CellRange.CellIterator)first.iterator(),
				    secondIt = (CellRange.CellIterator)second.iterator() ;
				firstIt.hasNext() ;)
		{
			firstSet.add(firstIt.next().getValue());
			secondSet.add(secondIt.next().getValue());
		}
		firstSet.remove(0.0);
		firstSet.retainAll(secondSet);

		return !firstSet.isEmpty();
	}
}
