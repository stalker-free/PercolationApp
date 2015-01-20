package hk;

import hk.cell.*;

import java.util.Iterator;

/**
 * Class for implementing lattice slicing.
 */
public class CellRange<T extends Comparable<T>> implements Iterable<Cell<T>>
{
	private int startX, startY;
	private int endX, endY;
	private Cell<T>[][] origin;

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

		public Cell<T> getNorth(Cell<T> notFound, int upperBound)
		{
			return (currentX > upperBound) ? origin[currentX - 1][currentY] : notFound;
		}

		public Cell<T> getWest(Cell<T> notFound, int lefterBound)
		{
			return (currentY > lefterBound) ? origin[currentX][currentY - 1] : notFound;
		}

		public Cell<T> getNorth(Cell<T> notFound)
		{
			return getNorth(notFound, startX);
		}

		public Cell<T> getWest(Cell<T> notFound)
		{
			return getWest(notFound, startY);
		}

		public Cell<T> getNorth()
		{
			return getNorth(null);
		}

		public Cell<T> getWest()
		{
			return getWest(null);
		}

		public Cell<T> getCell()
		{
			return this.getCell(this.get());
		}

		public Cell<T> getCell(Cell<T> cell)
		{
			return ReferenceCell.getCell(origin, cell);
		}

		public Cell<T> getReference()
		{
			return this.getReference(this.get());
		}

		public Cell<T> getReference(Cell<T> cell)
		{
			return ReferenceCell.getReference(origin, cell);
		}

		public Cell<T> get()
		{
			return origin[currentX][currentY];
		}

		public Cell<T> get(ReferenceCell<T> ref)
		{
			return ref.getValueFrom(origin);
		}

		public Cell<T> getNorthReference()
		{
			return new ReferenceCell<T>(currentX - 1, currentY);
		}

		public Cell<T> getWestReference()
		{
			return new ReferenceCell<T>(currentX, currentY - 1);
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
	public CellRange(Cell<T>[][] origin)
	{
		this(origin, 0, 0, origin.length, origin[0].length);
	}

	/**
	 * Construct the slice of lattice.
	 * @param origin original lattice.
	 * @param startX,startY left-top corner of the slice.
	 * @param endX,endY right-bottom corner of the slice.
	 */
	public CellRange(Cell<T>[][] origin, int startX, int startY, int endX, int endY)
	{
		assert startX >= 0 && startY >= 0 && endX >= 0 && endY >= 0;
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
}