package hk;

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
}
