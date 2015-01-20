package hk.cell;

public interface Cell<T extends Comparable<T>>
{
	T getValue();
	void setValue(T value);
	boolean isReference();
}
