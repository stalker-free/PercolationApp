package hk;

public class ReferenceCell<T extends Comparable<T>> implements Cell<T>
{
	private Cell<T> ref;

	public ReferenceCell(Cell<T> ref){
		this.ref = ref;
	}

	@Override
	public Cell<T> getReference(){
		return ref;
	}

	public Cell<T> getCell()
	{
		Cell<T> prev, next = getReference();

		do{
			prev = next;
			next = prev.getReference();
		}while(prev != next);

		return next;
	}

	@Override
	public T getValue()
	{
		return getCell().getValue();
	}

	@Override
	public void setValue(T value)
	{
		getCell().setValue(value);
	}

	@Override
	public int compareTo(Cell<T> o)
	{
		return getValue().compareTo(o.getValue());
	}

	@Override
	public Cell<T> getZeroCell(){
		return getCell().getZeroCell();
	}
}
