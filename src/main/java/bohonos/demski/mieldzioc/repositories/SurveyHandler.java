/*
 * 
 */
package bohonos.demski.mieldzioc.repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bohonos.demski.mieldzioc.survey.Survey;

/**
 *
 * @author Andrzej Bohonos
 */
public class SurveyHandler {
    
    public static final int IN_PROGRESS = 0;
    public static final int ACTIVE = 1;
    public static final int INACTIVE = 2;
    public static final int NO_SURVEY = -1;
    
 
            

    private ConcurrentHashMap<Survey, Integer> surveys = new ConcurrentHashMap<Survey, Integer>();
    private ConcurrentHashMap<String, Survey> surveysId = new ConcurrentHashMap<String, Survey>();
    
   
    
    
    /**
     * get new copy survey with given id
     * @param idOfSurveys survey id
     * @return copy of survey with given id
     * @throws java.lang.CloneNotSupportedException
     */
    public Survey provideSurvey(String idOfSurveys) throws CloneNotSupportedException
    {
        Survey survey = surveysId.get(idOfSurveys).clone();
        return survey;
    }
    
    /**
     * returns survey with given id
     * @param idOfSurveys survey id
     * @return reference to survey with given id
     */
    public Survey getSurvey(String idOfSurveys)
    {
        return surveysId.get(idOfSurveys);
    }
    
    /**
     * @author Dominik Demski
     * Uaktualnij szablon ankiety.
     * @param survey zaktualizowany szablon.
     * @return false, jeœli nie ma szablonu o podanym id lub jeœli szablon nie ma statusu IN_PROGRESS,
     * w przeciwnym przypadku true.
     */
    public boolean updateSurvey(Survey survey){
    	String idOfSurveys = survey.getIdOfSurveys();
    	int status = getSurveyStatus(idOfSurveys);
    	if(status == NO_SURVEY || status != IN_PROGRESS){
    		return false;
    	}
    	Survey toChange = surveysId.get(idOfSurveys);
    	surveys.remove(toChange);
    	surveysId.remove(idOfSurveys);
    	surveys.put(survey, IN_PROGRESS);
    	surveysId.put(idOfSurveys, survey);
    	return true;
    }
    
    
    
    /**
     * load survey to map
     * @param survey survey to load
     * @param status status of loading survey
     * @return true, if survey was added or false, if such survey already exists in map
     */
    public boolean loadSurveyTemplate(Survey survey, int status)
    {
        if (surveys.containsKey(survey))
            return false;
        else
        {
        	surveys.put(survey, status);
            surveysId.put(survey.getIdOfSurveys(), survey);
            return true;
        }
    }
    
    /**
     * get status of particular survey
     * @param idOfSurveys id of survey
     * @return status of survey, if such survey exists or -1 otherwise
     */
    public int getSurveyStatus(String idOfSurveys)
    {
            if (surveysId.containsKey(idOfSurveys))
            {
                Survey survey = surveysId.get(idOfSurveys);
                return surveys.get(survey);
            }
            else
            {
                return NO_SURVEY;
            }
    }
    
    /**
     * status of particular survey
     * @param survey given survey
     * @return status of survey, if such survey exists or -1 otherwise
     */
    public int getSurveyStatus(Survey survey)
    {
        if (surveys.containsKey(survey))
        {
            return surveys.get(survey);
        }
        else
        {
            return NO_SURVEY;
        }
    }
    
    /**
     * @author Dominik Demski
     * Zmienia status grupy ankiet.
     * @param idOfSurveys id grupy ankiet.
     * @param nowy status (patrz sta³e w klasie SurveyHandler).
     * @return true, jeœli zmieniono status, false, jeœli nie ma ankiety o zadanym id lub
     * id statusu jest niepoprawne.
     */
    public boolean setSurveyStatus(String idOfSurveys, int status){
        if (surveysId.containsKey(idOfSurveys)){
        	if(status == IN_PROGRESS || status == ACTIVE || status == INACTIVE){            		
            Survey survey = surveysId.get(idOfSurveys);
            surveys.put(survey, status);
            return true;
        	}
        }
        return false;      
    	}
    
    
    /**
     * returns map of surveys with given status
     * @param status given status
     * @return map of surveys and their status
     */
    public Map<Survey,Integer> getStatusSurveys(int status)
    {
        Map<Survey,Integer> statusSurveys = new HashMap<Survey,Integer>();
        for (Map.Entry<Survey, Integer> entry : surveys.entrySet()) {
            if (entry.getValue().equals(status)) {
                statusSurveys.put(entry.getKey(), entry.getValue());
            }
        }
        return statusSurveys;
    }
    
    /**
     * returns map of surveysId and surveys with given status
     * @param status given status
     * @return map of surveysId and surveys
     */
    public Map<String,Survey> getStatusSurveysId(int status)
    {
        Map<String, Survey> statusSurveysId = new HashMap<String, Survey>();
        for (Map.Entry<Survey, Integer> entry : surveys.entrySet()) {
            if (entry.getValue().equals(status)) {
                statusSurveysId.put(entry.getKey().getIdOfSurveys(), entry.getKey());
            }
        }
        return statusSurveysId;
    }     
}
