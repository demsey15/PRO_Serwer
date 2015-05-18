package bohonos.demski.mieldzioc.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.rits.cloning.Cloner;

/**
 * 
 * @author Dominik Demski
 *
 */
public class MultipleChoiceQuestion extends Question{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> answers = new ArrayList<String>();
	private List<Integer> userAnswers = new ArrayList<Integer>();
	
	/**
	 * Create new MultipleChoiceQuestion object with obligatory status as true.
	 * @param question text of question
	 */
	public MultipleChoiceQuestion(String question){
		super(question);
	}
	/**
	 * Create new MultipleChoiceQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 */
	public MultipleChoiceQuestion(String question, boolean obligatory){
		super(question, obligatory);
	}
	
	/**
	 * Create new MultipleChoiceQuestion object.
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param answers lists of question's answers
	 */
	public MultipleChoiceQuestion(String question, boolean obligatory, List<String> answers){
		super(question, obligatory);
		this.answers = answers;
	}
	
	/**
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param errorMessage message to be provided to the user when the answer is wrong or there is no answer
	 * @param hint hint for the user connected with this question
	 */
	public MultipleChoiceQuestion(String question, boolean obligatory, String errorMessage, String hint) {
		super(question, obligatory, errorMessage, hint);
	}
	
	/**
	 * @param question text of question
	 * @param obligatory true if answer this question is obligatory.
	 * @param errorMessage message to be provided to the user when the answer is wrong or there is no answer
	 * @param hint hint for the user connected with this question
	 * @param answers lists of question's answers
	 */
	public MultipleChoiceQuestion(String question, boolean obligatory, String errorMessage, String hint, List<String> answers) {
		super(question, obligatory, errorMessage, hint);
		this.answers = answers;
	}
	

	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#isAnswered()
	 */
	@Override
	public boolean isAnswered() {
		return userAnswers.size() > 0;
	}
	
	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#setUserAnswers(java.util.List)
	 */
	@Override
	public boolean setUserAnswers(List<String> text) {
		userAnswers.clear();
		if(text == null) return false;
		int answerIndex;
		for(String answer : text){
			if((answerIndex = answers.indexOf(answer)) == -1){
				userAnswers.clear();
				return false;
			}
			else{
				userAnswers.add(answerIndex);
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see bochonos.demski.mieldzioc.questions.Question#getAnswersAsStringList()
	 */
	@Override
	public List<String> getAnswersAsStringList() {
		return answers;
	}
	
	/**
	 * Add answer to this question.
	 * @param answer answer to add.
	 */
	public void addAnswer(String answer){
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
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass() != o.getClass()) return false;
		
		MultipleChoiceQuestion o2 = (MultipleChoiceQuestion) o;
		
		return super.equals(o2) && Objects.equals(answers, o2.answers) && 
				Objects.equals(userAnswers, o2.userAnswers);
	}
	
	@Override
	public MultipleChoiceQuestion clone() throws CloneNotSupportedException {
		return (new Cloner()).deepClone(this);
	}
	
	/**
	 * Zwraca listê odpowiedzi udzielonych przez u¿ytkownika.
	 */
	@Override
	public List<String> getUserAnswersAsStringList() {
		List<String> list = new ArrayList<String>(userAnswers.size());
		if(isAnswered()){
			for(Integer i : userAnswers){
				list.add(answers.get(i));
			}
		}
		return list;
	}
}
