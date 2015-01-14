package hk;

import java.util.Comparator;

public class IntegerCell implements Cell, Comparable<IntegerCell>
{
	private int value;

	public IntegerCell(int value){
		setValue(value);
	}

	public IntegerCell(Cell cell)
	{
		setValue(cell);
	}

	@Override
	public int getValue(){
		return value;
	}

	@Override
	public void setValue(int value)
	{
		this.value = value;
	}

	public void setValue(Cell cell)
	{
		this.value = ((IntegerCell)(cell)).value;
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

		IntegerCell that = (IntegerCell)o;

		return value == that.value;
	}

	@Override
	public int hashCode(){
		return value;
	}

	public static Comparator<Cell> comparator()
	{
		return new Comparator<Cell>() {
			@Override
			public int compare(Cell o1, Cell o2){
				IntegerCell c1 = (IntegerCell)o1, c2 = (IntegerCell)o2;
				if(c1.value < c2.value) return -1;
				else if(c1.value == c2.value) return 0;
				return 1;
			}
		};
	}

	@Override
	public int compareTo(IntegerCell o){
		if(this.value < o.value) return -1;
		else if(this.value == o.value) return 0;
		return 1;
	}
}
