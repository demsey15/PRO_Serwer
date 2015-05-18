/**
 * 
 */
package bohonos.demski.mieldzioc.constraints;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Dominik Demski
 * 
 */
public class NumberConstraint implements IConstraint, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double minValue;
	private Double maxValue;
	private boolean mustBeInteger;
	private Double notEquals;
	private boolean notBetweenMaxAndMinValue;

	
	/**
	 * Create NumberConstraint object.
	 * @param minValue minimum value of checked number (if there isn't any, put null).
	 * @param maxValue maximum value of checked number (if there isn't any, put null).
	 * @param mustBeInteger true if checked number must be integer.
	 * @param notEquals number which of checked number musn't be equal (if there isn't any, put null).
	 * @param notBetweenMaxAndMinValue true if checked number musn't be in [minValue, maxValue]
	 */
	public NumberConstraint(Double minValue, Double maxValue, boolean mustBeInteger, Double notEquals,
			boolean notBetweenMaxAndMinValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mustBeInteger = mustBeInteger;
		this.notEquals = notEquals;
		this.notBetweenMaxAndMinValue = notBetweenMaxAndMinValue;
	}
	
	/**
	  * Create NumberConstraint object with notBetweenMaxAndMinValue as false.
	 * @param minValue minimum value of checked number (if there isn't any, put null).
	 * @param maxValue maximum value of checked number (if there isn't any, put null).
	 * @param mustBeInteger true if checked number must be integer.
	 * @param notEquals number which of checked number musn't be equal (if there isn't any, put null).
	 */
	public NumberConstraint(Double minValue, Double maxValue, boolean mustBeInteger, Double notEquals) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.mustBeInteger = mustBeInteger;
		this.notEquals = notEquals;
		this.notBetweenMaxAndMinValue = false;
	}
	
	public Double getMinValue() {
		return minValue;
	}



	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}



	public Double getMaxValue() {
		return maxValue;
	}



	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}



	public boolean isMustBeInteger() {
		return mustBeInteger;
	}



	public void setMustBeInteger(boolean mustBeInteger) {
		this.mustBeInteger = mustBeInteger;
	}



	public Double getNotEquals() {
		return notEquals;
	}



	public void setNotEquals(Double notEquals) {
		this.notEquals = notEquals;
	}



	public boolean isNotBetweenMaxAndMinValue() {
		return notBetweenMaxAndMinValue;
	}



	public void setNotBetweenMaxAndMinValue(boolean notBetweenMaxAndMinValue) {
		this.notBetweenMaxAndMinValue = notBetweenMaxAndMinValue;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.constraints.IConstraint#checkCorrectness(java.lang.String)
	 */
	public boolean checkCorrectness(String expression) {
		Double toCheck = 0.0;
		try{
			toCheck = Double.valueOf(expression);
		}
		catch(NumberFormatException e){
			return false;
		}
		if(notEquals != null){
			if(toCheck.equals(notEquals)) return false;
		}
		if(! notBetweenMaxAndMinValue){
			if(minValue != null){
				if(toCheck.compareTo(minValue) < 0) return false;
			}
			if(maxValue != null){
				if(toCheck.compareTo(maxValue) > 0) return false;
			}
		}
		else{
			if(minValue != null && maxValue != null){
				if(toCheck.compareTo(minValue) >= 0 && toCheck.compareTo(maxValue) <= 0) return false;
			}
			else{
				if(minValue != null){
					if(toCheck.compareTo(minValue) >= 0) return false;
				}
				if(maxValue != null){
					if(toCheck.compareTo(maxValue) <= 0) return false;
				}
			}
		}
		if(mustBeInteger){
			if((toCheck % (toCheck.intValue())) != 0) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		NumberConstraint o2 = (NumberConstraint) o;
		
		return Objects.equals(minValue, o2.minValue) && Objects.equals(maxValue, o2.maxValue)
				&& mustBeInteger == o2.mustBeInteger && Objects.equals(notEquals, o2.notEquals) 
				&& notBetweenMaxAndMinValue == o2.notBetweenMaxAndMinValue;
	}
}
