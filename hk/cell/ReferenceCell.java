package hk.cell;

public class ReferenceCell<T extends Comparable<T>> implements Cell<T>
{
	private int x, y;

	public ReferenceCell(int x, int y){
		this.x = x;
		this.y = y;
	}

	public ReferenceCell(ReferenceCell<T> ref){
		this(ref.x, ref.y);
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

	public static <T extends Comparable<T>> Cell<T> getReference(Cell<T>[][] lattice, Cell<T> cell)
	{
		if(!cell.isReference()) return cell;
		return getUnsafeReference(lattice, cell);
	}

	public static <T extends Comparable<T>> Cell<T> getCell(Cell<T>[][] lattice, Cell<T> cell)
	{
		if(!cell.isReference()) return cell;
		ReferenceCell<T> ref = (ReferenceCell<T>)getUnsafeReference(lattice, cell);
		return ref.getValueFrom(lattice);
	}

	public Cell<T> getValueFrom(Cell<T>[][] lattice)
	{
		return lattice[getX()][getY()];
	}
}
