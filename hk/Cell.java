package hk;

public interface Cell<T extends Comparable<T>> extends Comparable<Cell<T>>
{
	T getValue();
	void setValue(T value);
	Cell<T> getCell();
	Cell<T> getReference();
	Cell<T> getZeroCell();
}
