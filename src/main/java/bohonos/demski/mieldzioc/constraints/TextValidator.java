/**
 * 
 */
package bohonos.demski.mieldzioc.constraints;

/**
 * @author Dominik Demski
 * 
 */
public class TextValidator {

	/**
	 * Assert if given text complies with constraint.
	 * @param text text to validate.
	 * @param constraint constraint to take into account.
	 * @return true if text is correct (according to constraint) or there is no constraint,
	 * otherwise false.
	 */
	public static boolean validate(String text, IConstraint constraint){
		if(constraint == null) return true;
		return constraint.checkCorrectness(text);
	}
}
