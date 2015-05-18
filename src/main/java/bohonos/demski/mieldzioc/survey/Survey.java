/**
 * 
 */
package bohonos.demski.mieldzioc.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.questions.Question;

import com.rits.cloning.Cloner;

/**
 * @author Andrzej Bohonos
 *
 */
public class Survey implements Serializable, Cloneable {
    
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Question> questions = new ArrayList<Question>();
    private GregorianCalendar startTime = null;
    private GregorianCalendar finishTime = null;
    private Interviewer interviewer;
    private String idOfSurveys;
    private String title;
    private String description;
    private String summary;
    private int numberOfSurvey;
    
    /**
     * start filling new survey
     * @return true iff action was successful
     */
    public boolean startSurvey()
    {
        if (startTime==null && finishTime==null)
        {
            startTime = new GregorianCalendar();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public GregorianCalendar getStartTime() {
        return startTime;
    }
    
    public GregorianCalendar getFinishTime() {
        return finishTime;
    }  
    
    /**
     * end filling new survey
     * @return true iff action was successful
     */
    public boolean finishSurvey()
    {
        if (startTime!=null && finishTime==null)
        {
            finishTime = new GregorianCalendar();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * status of survey
     * @return true iff survey is started
     */
    public boolean isStarted() 
    {
        if (startTime==null)
            return false;
        else
            return true;
    }
    
    /**
     * status of survey
     * @return true iff survey is finished
     */
    public boolean isFinished()
    {
        if (finishTime==null)
                return false;
        else
                return true;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description) 
    {
        this.description = description;
    }
        
    public String getSummary()
    {
        return summary;
    }
    
    public void setSummary(String summary)
    {
        this.summary = summary;
    }
    
    public String getIdOfSurveys()
    {
        return idOfSurveys;
    }
    
    public void setIdOfSurveys(String idOfSurveys)
    {
        this.idOfSurveys = idOfSurveys;
    }
    
    public int getNumberOfSurvey()
    {
        return numberOfSurvey;
    }
    
    public void setNumberOfSurvey(int numberOfSurvey)
    {
        this.numberOfSurvey = numberOfSurvey;
    }
    
    public Interviewer getInterviewer()
    {
        return interviewer;
    }
    
    public void setInterviewer(Interviewer interviewer)
    {
        this.interviewer = interviewer;
    }
    
    /**
     * @return size of questions list
     */
    public int questionListSize()
    {
        return questions.size();
    }
    
    /**
     * add new question in the end of list
     * @param question new question to add
     * @return true iff action was successful
     */
    public boolean addQuestion(Question question)
    {
        return questions.add(question);
    }
    
    /**
     * add new question in the particular place
     * @param index place where do add
     * @param question new question to add
     * @return true iff action was successful
     */
    public boolean addQuestion(int index, Question question)
    {
        if (index<questions.size())
        {
            questions.add(index, question);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * clear whole list of questions
     */
    public void questionListClear()
    {
        questions.clear();
    }
    
    /**
     * remove question from particular place
     * @param index place from where we remove
     * @return romoved question
     */
    public Question removeQuestion(int index)
    {
        return questions.remove(index);
    }
    
    /**
     * remove question from the list
     * @param question questiom to remove
     * @return true iff action was successful
     */
    public boolean removeQuestion(Question question)
    {
        return questions.remove(question);
    }
    
    /**
     * get question of particular index without removing it
     * @param index index of question
     * @return question of given index
     */
    public Question getQuestion(int index)
    {
    	return questions.get(index);
    }
    
    /**
     * check if list contains particular question
     * @param question question we check
     * @return true iff list contains this question
     */
    public boolean questionListContains(Question question)
    {
        return questions.contains(question);
    }
    
    /**
     * return index of particular question
     * @param question question we check
     * @return index of question, if list contains this question or -1 otherwise
     */
    public int indexOfQuestion(Question question)
    {
        if (questions.contains(question))
        {
            return questions.indexOf(question);
        }
        else
        {
            return -1;
        }
    }
    
    public Survey(List<Question> questions, GregorianCalendar startTime, GregorianCalendar finishTime, Interviewer interviewer, String idOfSurveys, String title, String description, String summary, int numberOfSurvey)
    {
        this.questions = questions;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.idOfSurveys = idOfSurveys;
        this.interviewer = interviewer;
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.numberOfSurvey = numberOfSurvey;
    }
    
    /**
	 * Stwórz now¹ ankietê, podaj¹c jako argument ankietera tworz¹cego ankietê. 
	 * Jeœli tworzony jest szablon ankiety, stworzon¹ ankietê nale¿y przekazaæ klasie SurveyHandler,
	 * ona nada szablonowi numer grupy ankiet. 
	 * @author Dominik Demski
	 * @param interviewer - Ankieter, który stworzy³ dan¹ ankietê.
	 */
    public Survey(Interviewer interviewer) {
		this.interviewer = interviewer;
	}
        
    /**
     * @return true iff question list is empty
     */
    public boolean questionListEmpty()
    {
        return questions.isEmpty();
    }
    
    /**
     * replace question of given index by other one
     * @param index place, where we want to make replacement
     * @param question new question
     * @return old question
     */
    public Question setQuestion(int index, Question question)
    {
        return questions.set(index, question);
    }
    
    /**
     * overwritten equals method
     * @param o other object to compare
     * @return true iff both are surveys with the some ids and numbers 
     */
    @Override
    public boolean equals(Object o)
    {
        if (this==o) 
            return true;
        if (o==null)
            return false;
        if (this.getClass()!=o.getClass())
            return false;
        Survey otherSurvey = (Survey)o;
        if (this.idOfSurveys.equals(otherSurvey.getIdOfSurveys()) && this.numberOfSurvey==otherSurvey.getNumberOfSurvey())
            return true;
        else
            return false;
    }
    
    /**
     * 
     * @return
     * @throws CloneNotSupportedException 
     */
    @Override
    public Survey clone() throws CloneNotSupportedException {
	return (new Cloner()).deepClone(this);
    }
    
}
