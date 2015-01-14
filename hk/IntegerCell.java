package hk;

public class IntegerCell implements Cell
{
	private int value;

	public IntegerCell(int value){
		this.value = value;
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

	@Override
	public String toString(){
		return "IntegerCell{" +
				"value=" + value +
				'}';
	}
}
