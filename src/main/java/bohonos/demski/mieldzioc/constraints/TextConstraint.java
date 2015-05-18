package bohonos.demski.mieldzioc.constraints;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dominik Demski
 * 
 */
public class TextConstraint implements IConstraint, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer minLength;
	private Integer maxLength;
	private Pattern regex;
	
	/**
	 * Create new TextConstraint object.
	 * @param minLength minimum length of String (if there isn't any, put null)
	 * @param maxLength maximum Length of String (if there isn't any, put null)
	 * @param regex regular expression to check (if there isn't any, put null)
	 * @throws IllegalArgumentException - throws when minLength > maxLength.
	 */
	public TextConstraint(Integer minLength, Integer maxLength, Pattern regex) throws IllegalArgumentException {
		if(minLength != null && maxLength != null){
			if(minLength > maxLength) throw new IllegalArgumentException("minLength should be lower or equal to maxLength");
		}
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.regex = regex;
	}

	public Integer getMinLength() {
		return minLength;
	}


	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}


	public Integer getMaxLength() {
		return maxLength;
	}


	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}


	public Pattern getRegex() {
		return regex;
	}


	public void setRegex(Pattern regex) {
		this.regex = regex;
	}

	/**
	 * 
	 */
	

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.constraints.IConstraint#checkCorrectness(java.lang.String)
	 */
	public boolean checkCorrectness(String expression){
		if(minLength != null){
			if(expression.length() < minLength) return false;
		}
		if(maxLength != null){
			if(expression.length() > maxLength) return false;
		}
		if(regex != null){
			Matcher matcher = regex.matcher(expression);
			return matcher.matches();
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		TextConstraint o2 = (TextConstraint) o;
		
		return Objects.equals(minLength, o2.minLength) && Objects.equals(maxLength, o2.maxLength)
				&& Objects.equals(regex.toString(), o2.regex.toString());
	}
}
