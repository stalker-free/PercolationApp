package hk.cell;

public class IntegerCell implements Cell<Integer>, Comparable<Cell<Integer>>
{
	private Integer value;

	public IntegerCell(int value){
		setValue(value);
	}

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

	public void setValue(Cell cell)
	{
		this.value = new Integer(String.valueOf(cell.getValue()));
	}

	@Override
	public String toString(){
		return "IntegerCell{" +
				"value=" + value +
				'}';
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