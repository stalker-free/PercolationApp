package hk.cell;

public class ReferenceCell<T extends Comparable<T>> implements Cell<T>
{
	private int x, y;

	/**
	 * Constructs the reference by plane coordinates.
	 */
	public ReferenceCell(int x, int y){
		if(x < 0 || y < 0)
		{
			throw new IllegalArgumentException();
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs the reference by copying plane coordinates from another.
	 * @param ref reference cell.
	 */
	public ReferenceCell(ReferenceCell<T> ref){
		this(ref.getX(), ref.getY());
	}

	@Override
	public T getValue(){ return null; }

	@Override
	public void setValue(T value){}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	@Override
	public boolean isReference(){
		return true;
	}

	/**
	 * Gets the last reference to value cell.
	 * @param lattice two-dimensional array of cells.
	 * @param cell starting cell.
	 * @return Reference to cell.
	 * @throws java.lang.ClassCastException if starting
	 * cell is not instance of ReferenceCell class.
	 */
	private static <T extends Comparable<T>>
		Cell<T> getUnsafeReference(Cell<T>[][] lattice, Cell<T> cell)
	{
		Cell<T> result = cell;
		ReferenceCell<T> ref = (ReferenceCell<T>)result;
		while(result.isReference())
		{
			// The cell is the reference to another cell.
			ref = (ReferenceCell<T>)result;
			result = ref.getValueFrom(lattice);
		}
		return ref;
	}

	/**
	 * Gets the last reference to value cell.
	 * @param lattice two-dimensional array of cells.
	 * @param cell starting cell.
	 * @return Reference to cell.
	 */
	public static <T extends Comparable<T>> Cell<T> getReference(Cell<T>[][] lattice, Cell<T> cell)
	{
		if(!cell.isReference()) return cell;
		return getUnsafeReference(lattice, cell);
	}

	/**
	 * Gets the value cell.
	 * @param lattice two-dimensional array of cells.
	 * @param cell starting cell.
	 * @return The value cell.
	 */
	public static <T extends Comparable<T>> Cell<T> getCell(Cell<T>[][] lattice, Cell<T> cell)
	{
		if(!cell.isReference()) return cell;
		ReferenceCell<T> ref = (ReferenceCell<T>)getUnsafeReference(lattice, cell);
		return ref.getValueFrom(lattice);
	}

	/**
	 * Gets the cell from the given lattice by coordinates.
	 * @param lattice two-dimensional array of cells.
	 * @return Reference to lattice's cell.
	 */
	public Cell<T> getValueFrom(Cell<T>[][] lattice)
	{
		return lattice[x][y];
	}

	@Override
	public String toString(){
		return "{x = " + x + ", y = " + y + '}';
	}
}
