package hk.cell;

/**
 * Implementation providing methods for manipulation with integer cell.
 */
public class IntegerCell implements Cell<Integer>, Comparable<Cell<Integer>>
{
	private Integer value;

	/**
	 * Constructs cell from given integer.
	 */
	public IntegerCell(int value){
		setValue(value);
	}

	/**
	 * Constructs cell by taking integer from given cell.
	 * @param cell non-reference cell.
	 */
	public IntegerCell(Cell cell)
	{
		setValue(cell);
	}

	@Override
	public Integer getValue(){
		return value;
	}

	@Override
	public void setValue(Integer value)
	{
		this.value = value;
	}

	/**
	 * Sets the underlying value by receiving it from another cell.
	 * @param cell non-reference cell.
	 */
	public void setValue(Cell cell)
	{
		Object value = cell.getValue();
		if(cell.isReference() || value == null)
		{
			throw new IllegalArgumentException();
		}
		this.value = new Integer(String.valueOf(value));
	}

	@Override
	public String toString(){
		return value.toString();
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}

		Cell<Integer> that = (Cell<Integer>)o;

		return value.compareTo(that.getValue()) == 0;
	}

	@Override
	public int hashCode(){
		return value;
	}

	@Override
	public int compareTo(Cell<Integer> o){
		return value.compareTo(o.getValue());
	}

	@Override
	public boolean isReference(){
		return false;
	}
}
