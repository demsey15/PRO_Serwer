package bohonos.demski.mieldzioc.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * 
 * @author Dominik Demski
 *
 */
public class SurveysRepository {

	public ConcurrentHashMap<String, List<Survey>> surveys = new ConcurrentHashMap<String, List<Survey>>();
	
	/**
	 * Dodaje now� wype�nion� ankiet�, je�li nie by�o takiej grupy ankiet, to dodaje j�, ale tylko tutaj
	 * w SurveyHandler nie.
	 * @param survey
	 * @return false, je�li ankieta z danym numere jest ju� w repozytorium.
	 */
	public boolean addNewSurvey(Survey survey){
		if(!surveys.contains(survey.getIdOfSurveys())){
			surveys.put(survey.getIdOfSurveys(), new ArrayList<Survey>());
		}
		List<Survey> list = surveys.get(survey.getIdOfSurveys());
		if(list.contains(survey)) return false;
		else{
			list.add(survey);
		}
		return true;
	}
	
	/**
	 * Pobiera list� z wype�nionymi ankietami dla zadanej grupy ankiet.
	 * @param idOfSurveys id grupy ankiet.
	 * @return lista wype�nionych ankiet lub null, je�li nie ma takiej grupy ankiet (lub brak 
	 * wynik�w).
	 */
	public List<Survey> getFilledSurveys(String idOfSurveys){
		return surveys.get(idOfSurveys);
	}
	
	/**
	 * 
	 * @param interviewer
	 * @return list� z ankietami wype�nionymi przez danego ankietera.
	 */
	public List<Survey> getSurveysFilledByInterviewer(Interviewer interviewer){
		List<Survey> list = new ArrayList<Survey>();
		for(List<Survey> surveyList : surveys.values()){
			for(Survey survey : surveyList){
				if(survey.getInterviewer().equals(interviewer)){
					list.add(survey);
				}
			}
		}
		return list;
	}
}
