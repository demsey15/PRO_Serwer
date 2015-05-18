/**
 * 
 */
package bohonos.demski.mieldzioc.common;

import java.util.Objects;

/**
 * @author Dominik Demski
 * 
 */
public class Pair<T, U> {
	
	private T first;
	private U second;

	/**
	 * 
	 */
	public Pair() {
		first = null;
		second = null;
	}
	

	public Pair(T first){
		this(first, null);
	}
	
	public Pair(T first, U second){
		this.first = first;
		this.second = second;
	}
	
	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public U getSecond() {
		return second;
	}

	public void setSecond(U second) {
		this.second = second;
	}

	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		@SuppressWarnings("rawtypes")
		Pair o2 = (Pair) o;
		
		return Objects.equals(first, o2.first) && Objects.equals(second, o2.second);
	}
}
