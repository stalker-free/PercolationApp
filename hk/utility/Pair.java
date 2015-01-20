package hk.utility;

public class Pair implements Comparable<Pair>
{
	private int first;
	private int last;

	public Pair(){}

	public Pair(int first, int last){
		this.first = first;
		this.last = last;
	}

	public int getFirst(){
		return first;
	}

	public void setFirst(int first){
		this.first = first;
	}

	public int getLast(){
		return last;
	}

	public void setLast(int last){
		this.last = last;
	}

	@Override
	public int compareTo(Pair o)
	{
		if(first < o.first ) return -1;
		if(first > o.first) return 1;
		return Integer.compare(last, o.last);
	}
}
