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
public class OneChoiceQuestion extends Question {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> answers = new ArrayList<String>();
	private int userAnswer = -1;  //-1 = there is no user's answer
	private boolean isDropDownList;
	
	
	/**
	 * Create OneChoiceQuestion object with isDropDownList as false and obligatory as true.
	 * @param question text of question
	 */
	public OneChoiceQuestion(String question) {
		super(question);
		this.isDropDownList = false;
	}

	
	
	/**
	 * Create OneChoiceQuestion object with isDropDownList as false
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory
	 */
	public OneChoiceQuestion(String question, boolean obligatory) {
		super(question, obligatory);
	}
	
	/**
	 * Create new OneChoiceQuestion object with isDropDownList as false
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory
	 * @param answers lists of question's answers
	 * @throws NullPointerException jeœli przekazana lista odpowiedzi jest nullem.
	 */
	public OneChoiceQuestion(String question, boolean obligatory, List<String> answers){
		super(question, obligatory);
		if(answers == null) throw new NullPointerException("Lista odpowiedzi nie moze byæ nullem!");
		this.answers = answers;
	}
	
	/**
	 * Create new OneChoiceQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory
	 * @param answers lists of question's answers
	 * @param isDropDownList true if question should be presented as drop-down list
	 * @throws NullPointerException jeœli przekazana lista odpowiedzi jest nullem.
	 */
	public OneChoiceQuestion(String question, boolean obligatory, List<String> answers, boolean isDropDownList){
		super(question, obligatory);
		if(answers == null) throw new NullPointerException("Lista odpowiedzi nie moze byæ nullem!");
		this.answers = answers;
		this.isDropDownList = isDropDownList;
	}

	/**
	 * Create new OneChoiceQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param errorMessage message to be provided to the user when the answer is wrong or there is no answer
	 * @param hint hint for the user connected with this question
	 * @param isDropDownList true if question should be presented as drop-down list
	 */
	public OneChoiceQuestion(String question, boolean obligatory, String errorMessage, String hint, boolean isDropDownList) {
		super(question, obligatory, errorMessage, hint);
		this.isDropDownList = isDropDownList;
	}
	
	/**
	 * Create new OneChoiceQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param errorMessage message to be provided to the user when the answer is wrong or there is no answer
	 * @param hint hint for the user connected with this question
	 * @param isDropDownList true if question should be presented as drop-down list
	 * @param answers lists of question's answers
	 * @throws NullPointerException jeœli przekazana lista odpowiedzi jest nullem.
	 */
	public OneChoiceQuestion(String question, boolean obligatory, String errorMessage, String hint, boolean isDropDownList, List<String> answers) {
		super(question, obligatory, errorMessage, hint);
		if(answers == null) throw new NullPointerException("Lista odpowiedzi nie moze byæ nullem!");
		this.isDropDownList = isDropDownList;
		this.answers = answers;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#setUserAnswers(java.util.List)
	 */
	@Override
	public boolean setUserAnswers(List<String> text) {
		userAnswer = -1;  //reset user's answer
		if(text == null) return false;
		if(text.size() != 1) return false;
		int answer = answers.indexOf(text.get(0));
		if(answer == -1) return false;
		else{
			this.userAnswer = answer;
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#getAnswersAsStringList()
	 */
	@Override
	public List<String> getAnswersAsStringList() {
		return answers;
	}

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#isAnswered()
	 */
	@Override
	public boolean isAnswered() {
		return (userAnswer != -1);
	}
	
	/**
	 * Add answer to this question.
	 * @param answer answer to add.
	 */
	public void addAnswer(String answer){
		if(answer == null) throw new NullPointerException("Odpowiedz nie moze byc nullem");
		answers.add(answer);
	}
	
	/**
	 * Add answer to this question at particular position. Positions start form zero. Position mustn't be grater than
	 * the last existing position + 1.
	 * For example: we have already [ans1, ans2, ans3] positionOfAnswer 0, 1, 2, 3 are correct but the others not.
	 * Existing answers are shifted to the right. 
	 * @param answer answer to add.
	 * @return true if answers is added otherwise false
	 */
	public boolean addAnswer(String answer, int positionOfAnswer){
		if(answer == null) throw new NullPointerException("Odpowiedz nie moze byc nullem");
		if(positionOfAnswer < 0 || positionOfAnswer > answers.size()) return false;
		answers.add(positionOfAnswer, answer);
		return true;
	}
	
	/**
	 * Delete answer to this question.
	 * @param answerNumber number of answer to delete
	 * @return	true is answer has been deleted, false if there is no answer with given answerNumber
	 */
	public boolean deleteAnswer(int answerNumber){
		if(answerNumber >= answers.size() || answerNumber < 0) return false;
		answers.remove(answerNumber);
		return true;
	}
	/**
	 * Get number of an answer.
	 * @param answer answer text
	 * @return answer number or -1 if there is no answer with this number
	 */
	public int getAnswerNumber(String answer){
		return answers.indexOf(answer);
	}
	
	/**
	 * 
	 * @return true if this question should be performed as drop-down list
	 */
	public boolean getIsDropDownList(){
		return isDropDownList;
	}
	
	/**
	 * Set if this question should be performed as drop-down list.
	 * @param isDropDownList true if this question should be performed as drop-down list
	 */
	public void setIsDropDownList(boolean isDropDownList){
		this.isDropDownList = isDropDownList;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		OneChoiceQuestion o2 = (OneChoiceQuestion) o;
		
		return super.equals(o2) && Objects.equals(answers, o2.answers) && 
				Objects.equals(userAnswer, o2.userAnswer) && 
				this.isDropDownList == o2.isDropDownList;
	}
	
	@Override
	public OneChoiceQuestion clone() throws CloneNotSupportedException {
		return (new Cloner()).deepClone(this);
	}
	
	/**
	 * Zwraca odpowiedŸ udzielon¹ przez u¿ytkownika w postaci listy.
	 */
	@Override
	public List<String> getUserAnswersAsStringList() {
		List<String> list = new ArrayList<String>(1);
		if(isAnswered()) list.add(answers.get(userAnswer));
		return list;
	}


}
