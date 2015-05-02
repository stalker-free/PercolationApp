package hk.cell;

public class Cell
{
	private int value = 0;

	public Cell(){}

	public Cell(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return Integer.toString(value);
	}
}
