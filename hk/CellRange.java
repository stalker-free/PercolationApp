package hk;

import hk.cell.*;
import hk.util.TwoDimensionalPercolation;

import java.util.Iterator;

/**
 * Class for implementing lattice slicing.
 */
public class CellRange implements Iterable<Cell>
{
	private int startX, startY;
	private int endX, endY;
	private Cell[][] origin;
	private static final Cell ZERO_CELL = new IntegerCell(0);

	/**
	 * Iterator for this class.
	 */
	public class CellIterator implements Iterator<Cell>
	{
		private int currentX, currentY;
		private int iterationNum = 0;

		public CellIterator()
		{
			this.currentX = startX;
			this.currentY = startY;
		}

		@Override
		public boolean hasNext()
		{
			return (currentY < endY) && (currentX < endX) &&
				(iterationNum < ((endY - startY) * (endX - startX)));
		}

		@Override
		public Cell next()
		{
			// Go to next iteration
			int diff = iterationNum % (endY - startY);

			currentX = startX + (iterationNum - diff) / (endY - startY);
			currentY = startY + diff;

			++iterationNum;

			return origin[currentX][currentY];
		}

		@Override
		public void remove(){
			origin[currentX][currentY] = null;
		}

		public Cell getNorth()
		{
			return (currentX > 0) ? origin[currentX - 1][currentY] : ZERO_CELL;
		}

		public Cell getWest()
		{
			return (currentY > 0) ? origin[currentX][currentY - 1] : ZERO_CELL;
		}

		public Cell get()
		{
			return origin[currentX][currentY];
		}

		public void set(Cell value)
		{
			origin[currentX][currentY] = value;
		}

		public int getIterationNumber()
		{
			return iterationNum;
		}
	}

	@Override
	public Iterator<Cell> iterator(){
		return new CellIterator();
	}

	/**
	 * Construct the slice of whole lattice.
	 * @param origin original lattice.
	 */
	public CellRange(Cell[][] origin)
	{
		this(origin, 0, 0, origin.length, origin[0].length);
	}

	/**
	 * Construct the slice of lattice.
	 * @param origin original lattice.
	 * @param startX,startY left-top corner of the slice.
	 * @param endX,endY right-bottom corner of the slice.
	 */
	public CellRange(Cell[][] origin, int startX, int startY, int endX, int endY)
	{
		assert startX >= 0 && startY >= 0 && endX >= 0 && endY >= 0;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.origin = origin;
	}

	public int getStartY(){
		return startY;
	}

	public int getStartX(){
		return startX;
	}

	public int getEndY(){
		return endY;
	}

	public int getEndX(){
		return endX;
	}

	public static CellRange getEdge(Cell[][] lattice,
        TwoDimensionalPercolation byWhat, boolean inStart)
	{
		int where;
		switch(byWhat)
		{
			case BY_X:
				where = inStart ? 0 : (lattice.length - 1);
				return new CellRange(lattice, 0, where, lattice.length, where + 1);
			case BY_Y:
				where = inStart ? 0 : (lattice[0].length - 1);
				return new CellRange(lattice, where, 0, where + 1, lattice[0].length);
		}

		throw new IllegalArgumentException();
	}
}