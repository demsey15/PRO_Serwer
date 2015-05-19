/**
 * 
 */
package bohonos.demski.mieldzioc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.interviewer.InterviewerSurveyPrivileges;
import bohonos.demski.mieldzioc.repositories.SurveyHandler;
import bohonos.demski.mieldzioc.repositories.SurveysRepository;
import bohonos.demski.mieldzioc.repositories.Workers;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * @author Dominik Demski
 *
 */
public class ConnectionHandler implements Runnable{
	
	public final static int INVALID_NULL_REFERENCE = -3;
	public final static int BAD_DATA_FORMAT = -2;
	public final static int UNKNOWN_FAIL = -1;
	public final static int LOGIN_OK = 1;
	public final static int BAD_PASSWORD = 2;
	public final static int AUTHORIZATION_FAILED = 3;
	public final static int AUTHORIZATION_OK = 4;
	
	public final static int OPERATION_OK = 5;
	
	public final static int SEND_NEW_TEMPLATE = 10;
	public final static int TEMPLATE_ALREADY_EXISTS = 11;
	
	public final static int CHANGE_SURVEY_STATUS = 12;
	
	public final static int UPDATE_SURVEY_TEMPLATE = 13;
	public final static int SURVEY_UNEDITABLE = 14;
	public final static int LACK_OF_SURVEY_TEMPLATE_WITH_ID = 15;
	
	public final static int SEND_FILLED_SURVEYS = 16;
	public final static int SURVEY_INACTIVE = 17;
	
	public final static int GET_ACTIVE_SURVEY_TEMPLATE = 18;
	public final static int GET_INACTIVE_SURVEY_TEMPLATE = 19;
	public final static int GET_IN_PROGRESS_SURVEY_TEMPLATE = 20;
	
	public final static int ADD_NEW_INTERVIEWER = 21;
	public final static int ADD_NEW_ADMINISTRATOR = 22;
	
	public final static int GET_FILLED_SURVEYS = 23;
	public final static int SENDING_FILLED_SURVEYS = 24;
	
	public final static int GET_ALL_INTERVIEWERS = 25;
	public final static int GET_INTERVIEWER = 26;
	public final static int AUTHENTICATION = 27;
	public final static int DISMISS_INTERVIEWER = 28;
	public final static int BACK_TO_WORK_INTERVIEWER = 29;
	public final static int GET_SURVEYS_FILLED_BY_INTERVIEWER = 30;

	public final static int SEND_INTERVIEWER_PRIVILAGES = 31;
	public final static int GET_INTERVIEWER_PRIVILAGES = 32;
	public final static int GET_INTERVIEWER_CREATING_PRIVILIGES = 33;
	public final static int SET_INTERVIEWER_CREATING_PRIVILIGES = 34;
	
	public final static int GET_ACTIVE_TEMPLATES_ID_FOR_INTERVIEWER = 35; //pobierz ankiety, kt�re ankieter mo�e wype�nia�
	public final static int GET_EDITABLE_TEMPLATES_ID_FOR_INTERVIEWER = 36; //pobierz ankiety, kt�re ankieter mo�e edytowa�
	public final static int GET_SURVEY_TEMPLATE = 37;
	
	
	private Socket incoming;
	private Workers workers;
	private Interviewer interviewer;
	private String administrator;
	private SurveyHandler surveyHandler;
	private SurveysRepository surveysRepository;
	
	private Scanner in;
	private PrintWriter out;
	
	public ConnectionHandler(Socket incoming, Workers workers, SurveyHandler surveyHandler, 
			SurveysRepository surveysRepository) {
		this.incoming = incoming;
		this.workers = workers;
		this.surveyHandler = surveyHandler;
		this.surveysRepository = surveysRepository;
		try {
		in = new Scanner(incoming.getInputStream());
		out = new PrintWriter(incoming.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {		
		try {
			System.out.println("Chc� czyta� id");
			String id = readString(); //czytaj id
			System.out.println("Odczyta�em: " + id);
			char[] password = readString().toCharArray();	//czytaj has�o
			System.out.println("Odczyta�em: " + id + " " + new String(password));
			if(! workers.chceckPassword(id, password)){
				System.out.println("Z�e haslo");
				sendInt(BAD_PASSWORD);
			}
			else{
				sendInt(LOGIN_OK);
				System.out.println("haslo ok");
				interviewer = workers.getInterviewer(id);
				if(interviewer == null){
					System.out.println("pod��czy� si� administrator");
					this.administrator = id;
				}
				
				
				int choice = readInt();
				switch (choice) {
				case SEND_NEW_TEMPLATE:
					if(interviewer != null){
						if(!interviewer.getInterviewerPrivileges()){ //je�li ankieter nie mo�e tworzy� nowych ankiet
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
						else sendInt(AUTHORIZATION_OK);
					}
					else sendInt(AUTHORIZATION_OK);   //zalogowany jest administrator
					
					Object object = readObject();
					if(object == null) sendInt(INVALID_NULL_REFERENCE);
					else{
						Survey survey = (Survey) object;     //odbierz ankiet�
						System.out.println("Otrzyma�em nowy szablon ankiet: " + survey.getIdOfSurveys());
						if(!surveyHandler.loadSurveyTemplate(survey, SurveyHandler.IN_PROGRESS)){
							sendInt(TEMPLATE_ALREADY_EXISTS);
						}
						else{
							sendInt(OPERATION_OK);
						}
					}
					break;
				case CHANGE_SURVEY_STATUS:
					if(administrator == null){		//tylko administrator mo�e zmieni� status ankiety
						sendInt(AUTHORIZATION_FAILED);
					}
					else{
						sendInt(AUTHORIZATION_OK);
						String idOfSurveys = readString();
						int status = readInt();
						if(surveyHandler.setSurveyStatus(idOfSurveys, status)){
							sendInt(OPERATION_OK);
						}
						else sendInt(BAD_DATA_FORMAT);
					}
					break;
				case UPDATE_SURVEY_TEMPLATE:
					if(interviewer != null){
						if(!interviewer.getInterviewerPrivileges()){ //je�li ankieter nie mo�e tworzy� nowych ankiet
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
						else sendInt(AUTHORIZATION_OK);
					}
					else sendInt(AUTHORIZATION_OK);   //zalogowany jest administrator
					Object object2 =  readObject();
					if(object2 == null) sendInt(INVALID_NULL_REFERENCE);
					else{
						Survey survey2 = (Survey) object2;
						if(surveyHandler.updateSurvey(survey2)){
							sendInt(OPERATION_OK);
						}
						else sendInt(BAD_DATA_FORMAT);	//nie ma takiego szablonu lub szablon nie jest do edycji
					}
					break;
				case SEND_FILLED_SURVEYS:
					if(interviewer == null) sendInt(AUTHORIZATION_FAILED);  //tylko ankieter mo�e przes�a� wype�nione ankiety
					else{
						int numberOfSurveys = readInt();
						for(int i = 0; i < numberOfSurveys; i++){
							Object o = readObject();
							if(o == null) sendInt(INVALID_NULL_REFERENCE);
							else{
								Survey s = (Survey) o;
								if(surveyHandler.getSurveyStatus(s.getIdOfSurveys())	//je�eli mo�na dodawa� ankiety 
										== SurveyHandler.ACTIVE){
									if(!surveysRepository.addNewSurvey(s)){
										sendInt(BAD_DATA_FORMAT);
									}
									else sendInt(OPERATION_OK);
								}
								else sendInt(SURVEY_INACTIVE);
							}
								
							}
						}
					break;
				case GET_ACTIVE_SURVEY_TEMPLATE:
					Set<Survey> surveys = surveyHandler.
									getStatusSurveys(SurveyHandler.ACTIVE).keySet();
					sendInt(surveys.size());   //wy�lij, ile b�dzie przes�anych szablon�w
					for(Survey survey : surveys){
						sendObject(survey);
					}
					break;
				case GET_INACTIVE_SURVEY_TEMPLATE:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e otrzyma� niekatywne szablony
						sendInt(AUTHORIZATION_OK);
						Set<Survey> surveys2 = surveyHandler.
										getStatusSurveys(SurveyHandler.INACTIVE).keySet();
						sendInt(surveys2.size());   //wy�lij, ile b�dzie przes�anych szablon�w
						for(Survey survey : surveys2){
							sendObject(survey);
						}
					}
					break;
				case GET_IN_PROGRESS_SURVEY_TEMPLATE:
					if(interviewer != null){
						if(!interviewer.getInterviewerPrivileges()){//ankieter musi mie� uprawnienia
																	//do tworzenia ankiet
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
					}
					sendInt(AUTHORIZATION_OK);
					Set<Survey> surveys3 = surveyHandler.
									getStatusSurveys(SurveyHandler.IN_PROGRESS).keySet();
					sendInt(surveys3.size());   //wy�lij, ile b�dzie przes�anych szablon�w
					for(Survey survey : surveys3){
						sendObject(survey);
					}
					break;
				case ADD_NEW_ADMINISTRATOR:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e dodawa� administrator�w
						sendInt(AUTHORIZATION_OK);
						String adminId = readString();
						char[] adminPass = readString().toCharArray();
						if(workers.addNewAdministrator(adminId, adminPass)){
							sendInt(OPERATION_OK);
						}
						else sendInt(BAD_DATA_FORMAT);
					}
					break;
				case ADD_NEW_INTERVIEWER:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e dodawa� ankieter�w
						sendInt(AUTHORIZATION_OK);
						Object obj = readObject();
						if(obj == null) sendInt(INVALID_NULL_REFERENCE);
						else{
							Interviewer interviewer = (Interviewer) obj;
							char[] intPass = readString().toCharArray();
							if(workers.addNewInterviewer(interviewer, intPass)){
								sendInt(OPERATION_OK);
							}
							else sendInt(BAD_DATA_FORMAT);
						}
					}
					break;
				case GET_FILLED_SURVEYS:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e pobra� wyniki ankiet
						sendInt(AUTHORIZATION_OK);
						String idOfSurveys = readString();
						List<Survey> filledSurveys = surveysRepository.getFilledSurveys(idOfSurveys);
						if(filledSurveys == null){
							sendInt(BAD_DATA_FORMAT); //nie ma takiej grupy ankiet lub brak wynik�w
													//dla takiej grupy
						}
						else{
							sendInt(SENDING_FILLED_SURVEYS); //wysy�am ankiety (s� jakie� wype�nione)
							sendInt(filledSurveys.size()); //wysy�am liczb� wype�nionych ankiet
							for(Survey s : filledSurveys){  //wysy�am ankiety
								sendObject(s);
							}
							sendInt(OPERATION_OK);
						}
					}
					break;
				case GET_ALL_INTERVIEWERS:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{
						sendInt(AUTHORIZATION_OK);
						List<Interviewer> interviewers = workers.getAllInterviewers();
						sendInt(interviewers.size());
						for(Interviewer interv : interviewers){
							sendObject(interv);
						}
						sendInt(OPERATION_OK);
					}
					break;
				case GET_INTERVIEWER:
					String interviewerId = readString();
					if(administrator == null){
						if(!interviewer.getId().equals(interviewerId))	//ankietera mo�e odebra� tylko on sam albo administrator
							sendInt(AUTHORIZATION_FAILED);
							break;
					}
					sendInt(AUTHORIZATION_OK);
					Interviewer interv = workers.getInterviewer(interviewerId);
					if(interv == null){
						sendInt(BAD_DATA_FORMAT);
					}
					else{
						sendInt(OPERATION_OK);
						sendObject(interv);
					}
					break;
				case AUTHENTICATION:
					break;
				case DISMISS_INTERVIEWER:
					if(administrator == null){
						sendInt(AUTHORIZATION_FAILED); //tylko administrator mo�e zwolni� ankietera
						break;
					}
					else{
						sendInt(AUTHORIZATION_OK);
						String intervId = readString();
						Interviewer interviewer = workers.getInterviewer(intervId);
						if(interviewer == null){    //nie ma takiego ankietera
							sendInt(BAD_DATA_FORMAT);
						}
						else{
							sendInt(OPERATION_OK);
							GregorianCalendar relieveDay = (GregorianCalendar) readObject(); //odczytaj dat� zwolnienia
							interviewer.setRelieveDay(relieveDay);  //zwolnij pracownika
						}
					}
					break;
				case GET_SURVEYS_FILLED_BY_INTERVIEWER:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e pobra� wyniki ankiet
						sendInt(AUTHORIZATION_OK);
						String idOfInterviewer = readString();
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes�anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							List<Survey> filledSurveys = surveysRepository.
									getSurveysFilledByInterviewer(interviewer);
							sendInt(filledSurveys.size());
							for(Survey survey : filledSurveys){
								sendObject(survey);
							}
						}
					}
					break;
				case SEND_INTERVIEWER_PRIVILAGES:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo�e nada� uprawnienia
						sendInt(AUTHORIZATION_OK);
						String idOfInterviewer = readString();
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes�anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							InterviewerSurveyPrivileges privileges = 
									(InterviewerSurveyPrivileges) readObject();
							String idOfSurveys = readString();
							if(surveyHandler.getSurvey(idOfSurveys) == null){
								sendInt(BAD_DATA_FORMAT); //je�li nie ma takiej grupy ankiet
							}
							else{
								interviewer.setPrivilegesForInterviewer(idOfSurveys, privileges);
								sendInt(OPERATION_OK);
							}
						}
					}
					break;
				case GET_INTERVIEWER_PRIVILAGES:
					String idOfInterviewer = readString();
					if(interviewer != null && ! interviewer.getId().equals(idOfInterviewer)){
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo�e odczyta� administrator lub u�ytkownik sam o sobie
					}
					else{		//administrator mo�e odczyta� wszystkie uprawnienia
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes�anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							Map<String, InterviewerSurveyPrivileges> map = 
									interviewer.getIntervSurveyPrivileges();
							if(map == null){
								map = new HashMap<String, InterviewerSurveyPrivileges>();
							}
							sendObject(map); //wy�lij wszystkie uprawnienia u�ytkownika, nie przesy�a null
											//najwy�ej pust� map�
							}
						}
					break;
				case GET_INTERVIEWER_CREATING_PRIVILIGES:
					String idInterv = readString();
					if(interviewer != null && ! interviewer.getId().equals(idInterv)){
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo�e odczyta� administrator lub u�ytkownik sam o sobie
					}
					else{		//administrator mo�e odczyta� wszystkie uprawnienia
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idInterv);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes�anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							boolean create = interviewer.getInterviewerPrivileges();
							int can = (create) ? 1 : 0;
							sendInt(can); //przesy�a 1, je�li ankieter mo�e tworzy� ankiety, inaczej 0
							}
						}
					break;
				case SET_INTERVIEWER_CREATING_PRIVILIGES:
					String idIn = readString();
					if(administrator == null){
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo�e nada� administrator
					}
					else{		
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idIn);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes�anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							int can = readInt();
							interviewer.setInterviewerPrivileges(can == 1); //przesy�a 1, je�li ankieter mo�e tworzy� ankiety, inaczej 0
							}
						}
					break;
				case GET_ACTIVE_TEMPLATES_ID_FOR_INTERVIEWER:
					List<String> activeId = new ArrayList<String>();
					String idInter = readString();
					if(interviewer != null){
						if(!interviewer.getId().equals(idInter)){	//ankieter mo�e odebra� tylko swoje ankiety
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
					}
					sendInt(AUTHORIZATION_OK);
					Interviewer inter = workers.getInterviewer(idInter);
					if(inter == null){
						sendInt(BAD_DATA_FORMAT);
					}
					else{
						sendInt(OPERATION_OK);
						System.out.println("No wysy�am no...");
						activeId = surveyHandler.getSurveysIdForInterviewerToFill(inter);
						sendInt(activeId.size());
						for(String iddd : activeId){
							sendString(iddd);
						}
					}
					break;
				case GET_EDITABLE_TEMPLATES_ID_FOR_INTERVIEWER:
					List<String> editableId = new ArrayList<String>();
					String idInter2 = readString();
					if(interviewer != null){
						if(!interviewer.getId().equals(idInter2)){	//ankieter mo�e odebra� tylko swoje ankiety
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
					}
					sendInt(AUTHORIZATION_OK);
					Interviewer inter2 = workers.getInterviewer(idInter2);
					if(inter2 == null){
						sendInt(BAD_DATA_FORMAT);
						break;
					}
					else{
						sendInt(OPERATION_OK);
						editableId = surveyHandler.getSurveysIdForInterviewerToEdit(inter2);
						sendInt(editableId.size());
						for(String idd : editableId){
							sendString(idd);
						}
					}
					break;
				case GET_SURVEY_TEMPLATE:
					String survId = readString();
					Survey surv = surveyHandler.getSurvey(survId);
					if(surv == null){
						sendInt(BAD_DATA_FORMAT);
						break;
					}
					else{
						sendInt(OPERATION_OK);
						int status = surveyHandler.getSurveyStatus(survId);
						if(status == SurveyHandler.INACTIVE){
							if(administrator == null){
								sendInt(AUTHORIZATION_FAILED);
							}
							else{
								sendInt(AUTHORIZATION_OK);
								sendObject(surv);
							}
						}
					}
					break;
				default:
					break;
				}
				incoming.close();
		}
	}	 catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	private void sendInt(int i){
		System.out.println("Wysy�am: " + i);	
		out.println(i);
	}
	
	private void sendObject(Object obj){
		try {
			ObjectOutputStream outObject = new ObjectOutputStream(incoming.getOutputStream());
			outObject.writeObject(obj);
			System.out.println("Wysy�am obiekt " + obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendString(String s){
		out.println(s);
	}
	
	private int readInt(){
		try{
			int i = Integer.parseInt(in.nextLine());
			System.out.println("Odczyta�em: " + i);
			return i;
		} catch (NumberFormatException e) {
			return BAD_DATA_FORMAT;
		}
		
	}
	
	private String readString(){
		return in.nextLine();
	}
	
	private Object readObject(){
		try {
			ObjectInputStream inObj = new ObjectInputStream(incoming.getInputStream());
			return inObj.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
