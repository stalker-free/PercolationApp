package hk.cell;

/**
 * Interface representing basic operations with abstract cell.
 */
public interface Cell<T extends Comparable<T>>
{
	/**
	 * Retrieves underlying value from the non-reference cell.
	 * @return Cell's value, or null.
	 */
	T getValue();

	/**
	 * Lets cell to refer to the given value.
	 * @param value comparable value.
	 */
	void setValue(T value);

	/**
	 * Checks if the current cell is reference.
	 * @return <b>true</b>, if the cell refers to another cell, and <b>false</b> otherwise.
	 */
	boolean isReference();
}
