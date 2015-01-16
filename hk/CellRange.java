package hk;

import java.util.Iterator;

/**
 * Class for implementing lattice slicing.
 */
public class CellRange<T extends Comparable<T>> implements Iterable<Cell<T>>
{
	private int startX, startY;
	private int endX, endY;
	private Cell<T>[][] origin;
	private Cell<T> zeroCell;

	/**
	 * Iterator for this class.
	 */
	public class CellIterator implements Iterator<Cell<T>>
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
		public Cell<T> next()
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

		public Cell<T> getNorth()
		{
			return (currentX > startX) ? origin[currentX - 1][currentY] : zeroCell;
		}

		public Cell<T> getWest()
		{
			return (currentY > startY) ? origin[currentX][currentY - 1] : zeroCell;
		}

		public Cell<T> getSouth()
		{
			return (currentX < (endX - 1)) ? origin[currentX + 1][currentY] : zeroCell;
		}

		public Cell<T> getEast()
		{
			return (currentY > (endY - 1)) ? origin[currentX][currentY + 1] : zeroCell;
		}

		public Cell<T> get()
		{
			return origin[currentX][currentY];
		}

		public void set(Cell<T> value)
		{
			origin[currentX][currentY] = value;
		}

		public int getIterationNumber()
		{
			return iterationNum;
		}

		public int getCurrentX(){
			return currentX;
		}

		public int getCurrentY(){
			return currentY;
		}
	}

	@Override
	public Iterator<Cell<T>> iterator(){
		return new CellIterator();
	}

	/**
	 * Construct the slice of whole lattice.
	 * @param origin original lattice.
	 */
	public CellRange(Cell<T>[][] origin, Cell<T> zeroCell)
	{
		this(origin, 0, 0, origin.length, origin[0].length, zeroCell);
	}

	/**
	 * Construct the slice of lattice.
	 * @param origin original lattice.
	 * @param startX,startY left-top corner of the slice.
	 * @param endX,endY right-bottom corner of the slice.
	 */
	public CellRange(Cell<T>[][] origin, int startX, int startY, int endX, int endY, Cell<T> zeroCell)
	{
		assert startX >= 0 && startY >= 0 && endX >= 0 && endY >= 0;
		this.zeroCell = zeroCell.getZeroCell();
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.origin = origin;
	}

	public int getStartX(){
		return startX;
	}

	public int getStartY(){
		return startY;
	}

	public int getEndX(){
		return endX;
	}

	public int getEndY(){
		return endY;
	}

	public Cell<T> getZeroCell()
	{
		return zeroCell.getZeroCell();
	}
}