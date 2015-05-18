/**
 * 
 */
package bohonos.demski.mieldzioc.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.rits.cloning.Cloner;

/**
 * @author Dominik Demski
 * 
 */
public class ScaleQuestion extends Question {

	private static final long serialVersionUID = 1L;
	private int minValue;
	private int maxValue;
	private String maxLabel;
	private String minLabel;
	private int userAnswer = Integer.MIN_VALUE;  //Integer.MIN_VALUE - there is no user's answer
	
	
	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getMaxLabel() {
		return maxLabel;
	}

	public void setMaxLabel(String maxLabel) {
		this.maxLabel = maxLabel;
	}

	public String getMinLabel() {
		return minLabel;
	}

	public void setMinLabel(String minLabel) {
		this.minLabel = minLabel;
	}

	public int getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(int userAnswer) {
		this.userAnswer = userAnswer;
	}

	/**
	 * Create ScaleQuestion object with obligatory as true and without any labels.
	 * @param question text of question
	 * @param minValue the minimum value of this scale question
	 * @param maxValue the maximum value of this scale question
	 */
	public ScaleQuestion(String question, int minValue, int maxValue) {
		super(question);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	/**
	 * Create ScaleQuestion object without any labels.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param minValue the minimum value of this scale question
	 * @param maxValue the maximum value of this scale question
	 */
	public ScaleQuestion(String question, boolean obligatory, int minValue, int maxValue) {
		super(question, obligatory);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	/**
	 * Create ScaleQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param minValue the minimum value of this scale question
	 * @param maxValue the maximum value of this scale question
	 * @param minLabel description next to the minimum value
	 * @param maxLabel description next to the maximum value
	 */
	public ScaleQuestion(String question, boolean obligatory, int minValue, int maxValue, String minLabel, String maxLabel) {
		super(question, obligatory);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.minLabel = minLabel;
		this.maxLabel = maxLabel;
	}

	/**
	 * Create ScaleQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param errorMessage message to be provided to the user when the answer is wrong or there is no answer
	 * @param hint hint for the user connected with this question
	 * @param minValue the minimum value of this scale question
	 * @param maxValue the maximum value of this scale question
	 * @param minLabel description next to the minimum value
	 * @param maxLabel description next to the maximum value
	 */
	public ScaleQuestion(String question, boolean obligatory, String errorMessage, String hint, int minValue, 
			int maxValue, String minLabel, String maxLabel) {
		super(question, obligatory, errorMessage, hint);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.minLabel = minLabel;
		this.maxLabel = maxLabel;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#setUserAnswers(java.util.List)
	 */
	@Override
	public boolean setUserAnswers(List<String> text) {
		userAnswer = Integer.MIN_VALUE;     //reset user's answer
		if(text == null) return false;
		if(text.size() != 1) return false;
		int answer = 0;
		try{
			answer = Integer.parseInt(text.get(0));
		}
		catch(NumberFormatException e){
			return false;
		}
		if(answer < minValue || answer > maxValue) return false;
		userAnswer = answer;
		return true;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#getAnswersAsStringList()
	 */
	@Override
	public List<String> getAnswersAsStringList() {
		List<String> result = new ArrayList<String>(maxValue - minValue + 1);
		for(int i = minValue; i <= maxValue; i++){
			result.add(String.valueOf(i));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#isAnswered()
	 */
	@Override
	public boolean isAnswered() {
		return userAnswer != Integer.MIN_VALUE;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		ScaleQuestion o2 = (ScaleQuestion) o;
		
		return super.equals(o2) && this.userAnswer == o2.userAnswer && Objects.equals(minValue, o2.minValue) && 
				Objects.equals(maxValue, o2.maxValue) && Objects.equals(this.maxLabel, o2.maxLabel) &&
				Objects.equals(minLabel, o2.minLabel);
	}
	
	@Override
	public ScaleQuestion clone() throws CloneNotSupportedException {
		return (new Cloner()).deepClone(this);
	}
	
	/**
	 * Zwraca odpowiedŸ udzielon¹ przez u¿ytkownika w postaci listy.
	 */
	@Override
	public List<String> getUserAnswersAsStringList() {
		List<String> list = new ArrayList<String>(1);
		if(isAnswered()) list.add(String.valueOf(userAnswer));
		return list;
	}

}
