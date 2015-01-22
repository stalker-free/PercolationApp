package hk.utility;

/**
 * Class combining 2 comparable value into one.
 * @param <A> type of the first field.
 * @param <B> type of the second field.
 */
public class Pair<A extends Comparable<A>,
		B extends Comparable<B>> implements Comparable<Pair<A, B>>
{
	private A first;
	private B last;

	/**
	 * Constructs new pair from 2 given objects.
	 */
	public Pair(A first, B last){
		this.first = first;
		this.last = last;
	}

	public A getFirst(){
		return first;
	}

	public void setFirst(A first){
		this.first = first;
	}

	public B getLast(){
		return last;
	}

	public void setLast(B last){
		this.last = last;
	}

	@Override
	public int compareTo(Pair<A, B> o)
	{
		int cmp = first.compareTo(o.first);
		if(cmp != 0)
		{
			return cmp;
		}
		return last.compareTo(o.last);
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}

		if(!(o instanceof Pair)){
			return false;
		}

		Pair<A, B> pair = (Pair<A, B>)o;

		return this.compareTo(pair) == 0;
	}

	@Override
	public int hashCode(){
		int result = 20;
		result = 31 * result + first.hashCode();
		result = 31 * result + last.hashCode();
		return result;
	}

	@Override
	public String toString(){
		return "Pair{first=" + first +
				", last=" + last + '}';
	}
}
