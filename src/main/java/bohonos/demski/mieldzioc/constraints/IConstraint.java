/**
 * 
 */
package bohonos.demski.mieldzioc.constraints;

/**
 * @author Dominik Demski
 * 
 */
public interface IConstraint {
	/**
	 * Check if an expression is eligible.
	 * @param expression
	 * @return true if checked expression is eligible otherwise false.
	 */
	public boolean checkCorrectness(String expression);
}
