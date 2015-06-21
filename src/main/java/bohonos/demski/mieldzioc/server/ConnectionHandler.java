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
import java.util.regex.Pattern;

import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.constraints.TextConstraint;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.interviewer.InterviewerSurveyPrivileges;
import bohonos.demski.mieldzioc.questions.DateTimeQuestion;
import bohonos.demski.mieldzioc.questions.GridQuestion;
import bohonos.demski.mieldzioc.questions.MultipleChoiceQuestion;
import bohonos.demski.mieldzioc.questions.OneChoiceQuestion;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.questions.ScaleQuestion;
import bohonos.demski.mieldzioc.questions.TextQuestion;
import bohonos.demski.mieldzioc.repositories.SurveyHandler;
import bohonos.demski.mieldzioc.repositories.SurveysRepository;
import bohonos.demski.mieldzioc.repositories.Workers;
import bohonos.demski.mieldzioc.survey.Survey;

import com.google.gson.Gson;

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
	
	public final static int GET_ACTIVE_TEMPLATES_ID_FOR_INTERVIEWER = 35; //pobierz ankiety, które ankieter mo¿e wype³niaæ
	public final static int GET_EDITABLE_TEMPLATES_ID_FOR_INTERVIEWER = 36; //pobierz ankiety, które ankieter mo¿e edytowaæ
	public final static int GET_SURVEY_TEMPLATE = 37;
	
	
	private Socket incoming;
	private Workers workers;
	private Interviewer interviewer;
	private String administrator;
	private SurveyHandler surveyHandler;
	private SurveysRepository surveysRepository;
	private Gson gson = new Gson();
	
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
			System.out.println("Chcê czytaæ id");
			String id = readString(); //czytaj id
			System.out.println("Odczyta³em: " + id);
			char[] password = readString().toCharArray();	//czytaj has³o
			System.out.println("Odczyta³em: " + id + " " + new String(password));
			if(! workers.chceckPassword(id, password)){
				System.out.println("Z³e haslo");
				sendInt(BAD_PASSWORD);
			}
			else{
				sendInt(LOGIN_OK);
				System.out.println("haslo ok");
				interviewer = workers.getInterviewer(id);
				if(interviewer == null){
					System.out.println("pod³¹czy³ siê administrator");
					this.administrator = id;
				}
				
				
				int choice = readInt();
				switch (choice) {
				case SEND_NEW_TEMPLATE:
					if(interviewer != null){
						if(!interviewer.getInterviewerPrivileges()){ //jeœli ankieter nie mo¿e tworzyæ nowych ankiet
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
						else sendInt(AUTHORIZATION_OK);
					}
					else sendInt(AUTHORIZATION_OK);   //zalogowany jest administrator
					
					//Object object = readObject();
					Survey object = receiveSurveyTemplate();
					if(object == null) sendInt(INVALID_NULL_REFERENCE);
					else{
						Survey survey = (Survey) object;     //odbierz ankietê
						System.out.println("Otrzyma³em nowy szablon ankiet: " + survey.getIdOfSurveys());
						if(!surveyHandler.loadSurveyTemplate(survey, SurveyHandler.IN_PROGRESS)){
							sendInt(TEMPLATE_ALREADY_EXISTS);
						}
						else{
							if(interviewer != null){ //NADAJ UPRAWNIENIA DO WYPELNIANIA ANKIETY OD RAZU PO PRZYSLANIU - DO WYKASOWANIA PO DODANIU MODULU ADMNISTRATORA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								Map<String, InterviewerSurveyPrivileges> p = interviewer.getIntervSurveyPrivileges();
								p.put(survey.getIdOfSurveys(), new InterviewerSurveyPrivileges(true, true, true, true));
								surveyHandler.setSurveyStatus(survey.getIdOfSurveys(), SurveyHandler.ACTIVE);
							}
							sendInt(OPERATION_OK);
						}
					}
					break;
				case CHANGE_SURVEY_STATUS:
					if(administrator == null){		//tylko administrator mo¿e zmieniæ status ankiety
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
						if(!interviewer.getInterviewerPrivileges()){ //jeœli ankieter nie mo¿e tworzyæ nowych ankiet
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
					if(interviewer == null) sendInt(AUTHORIZATION_FAILED);  //tylko ankieter mo¿e przes³aæ wype³nione ankiety
					else{
						sendInt(AUTHORIZATION_OK);
						
						Survey survey = receiveFilledSurvey();
							
						if(survey == null) sendInt(INVALID_NULL_REFERENCE);
						else{
							if(surveyHandler.getSurveyStatus(survey.getIdOfSurveys())	//je¿eli mo¿na dodawaæ ankiety 
										== SurveyHandler.ACTIVE){
								if(!surveysRepository.addNewSurvey(survey)){
									sendInt(BAD_DATA_FORMAT);
								}
								else sendInt(OPERATION_OK);
							}
							else sendInt(SURVEY_INACTIVE);
						}	
					}
					break;
				case GET_ACTIVE_SURVEY_TEMPLATE:
					Set<Survey> surveys = surveyHandler.
									getStatusSurveys(SurveyHandler.ACTIVE).keySet();
					sendInt(surveys.size());   //wyœlij, ile bêdzie przes³anych szablonów
					for(Survey survey : surveys){
						sendObject(survey);
					}
					break;
				case GET_INACTIVE_SURVEY_TEMPLATE:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo¿e otrzymaæ niekatywne szablony
						sendInt(AUTHORIZATION_OK);
						Set<Survey> surveys2 = surveyHandler.
										getStatusSurveys(SurveyHandler.INACTIVE).keySet();
						sendInt(surveys2.size());   //wyœlij, ile bêdzie przes³anych szablonów
						for(Survey survey : surveys2){
							sendObject(survey);
						}
					}
					break;
				case GET_IN_PROGRESS_SURVEY_TEMPLATE:
					if(interviewer != null){
						if(!interviewer.getInterviewerPrivileges()){//ankieter musi mieæ uprawnienia
																	//do tworzenia ankiet
							sendInt(AUTHORIZATION_FAILED);
							break;
						}
					}
					sendInt(AUTHORIZATION_OK);
					Set<Survey> surveys3 = surveyHandler.
									getStatusSurveys(SurveyHandler.IN_PROGRESS).keySet();
					sendInt(surveys3.size());   //wyœlij, ile bêdzie przes³anych szablonów
					for(Survey survey : surveys3){
						sendObject(survey);
					}
					break;
				case ADD_NEW_ADMINISTRATOR:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo¿e dodawaæ administratorów
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
					else{		//tylko administrator mo¿e dodawaæ ankieterów
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
					else{		//tylko administrator mo¿e pobraæ wyniki ankiet
						sendInt(AUTHORIZATION_OK);
						String idOfSurveys = readString();
						List<Survey> filledSurveys = surveysRepository.getFilledSurveys(idOfSurveys);
						if(filledSurveys == null){
							sendInt(BAD_DATA_FORMAT); //nie ma takiej grupy ankiet lub brak wyników
													//dla takiej grupy
						}
						else{
							sendInt(SENDING_FILLED_SURVEYS); //wysy³am ankiety (s¹ jakieœ wype³nione)
							sendInt(filledSurveys.size()); //wysy³am liczbê wype³nionych ankiet
							for(Survey s : filledSurveys){  //wysy³am ankiety
								sendFilledSurvey(s);
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
						if(!interviewer.getId().equals(interviewerId))	//ankietera mo¿e odebraæ tylko on sam albo administrator
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
						sendInt(AUTHORIZATION_FAILED); //tylko administrator mo¿e zwolniæ ankietera
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
							GregorianCalendar relieveDay = (GregorianCalendar) readObject(); //odczytaj datê zwolnienia
							interviewer.setRelieveDay(relieveDay);  //zwolnij pracownika
						}
					}
					break;
				case GET_SURVEYS_FILLED_BY_INTERVIEWER:
					if(administrator == null) sendInt(AUTHORIZATION_FAILED);
					else{		//tylko administrator mo¿e pobraæ wyniki ankiet
						sendInt(AUTHORIZATION_OK);
						String idOfInterviewer = readString();
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes³anym id
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
					else{		//tylko administrator mo¿e nadaæ uprawnienia
						sendInt(AUTHORIZATION_OK);
						String idOfInterviewer = readString();
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes³anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							InterviewerSurveyPrivileges privileges = 
									(InterviewerSurveyPrivileges) readObject();
							String idOfSurveys = readString();
							if(surveyHandler.getSurvey(idOfSurveys) == null){
								sendInt(BAD_DATA_FORMAT); //jeœli nie ma takiej grupy ankiet
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
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo¿e odczytaæ administrator lub u¿ytkownik sam o sobie
					}
					else{		//administrator mo¿e odczytaæ wszystkie uprawnienia
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idOfInterviewer);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes³anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							Map<String, InterviewerSurveyPrivileges> map = 
									interviewer.getIntervSurveyPrivileges();
							if(map == null){
								map = new HashMap<String, InterviewerSurveyPrivileges>();
							}
							sendObject(map); //wyœlij wszystkie uprawnienia u¿ytkownika, nie przesy³a null
											//najwy¿ej pust¹ mapê
							}
						}
					break;
				case GET_INTERVIEWER_CREATING_PRIVILIGES:
					String idInterv = readString();
					if(interviewer != null && ! interviewer.getId().equals(idInterv)){
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo¿e odczytaæ administrator lub u¿ytkownik sam o sobie
					}
					else{		//administrator mo¿e odczytaæ wszystkie uprawnienia
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idInterv);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes³anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							boolean create = interviewer.getInterviewerPrivileges();
							int can = (create) ? 1 : 0;
							sendInt(can); //przesy³a 1, jeœli ankieter mo¿e tworzyæ ankiety, inaczej 0
							}
						}
					break;
				case SET_INTERVIEWER_CREATING_PRIVILIGES:
					String idIn = readString();
					if(administrator == null){
						sendInt(AUTHORIZATION_FAILED); //uprawnienia mo¿e nadaæ administrator
					}
					else{		
						sendInt(AUTHORIZATION_OK);
						
						Interviewer interviewer = workers.getInterviewer(idIn);
						if(interviewer == null){
							sendInt(BAD_DATA_FORMAT); //nie ma ankietera o przes³anym id
							break;
						}
						else{
							sendInt(OPERATION_OK);
							int can = readInt();
							interviewer.setInterviewerPrivileges(can == 1); //przesy³a 1, jeœli ankieter mo¿e tworzyæ ankiety, inaczej 0
							}
						}
					break;
				case GET_ACTIVE_TEMPLATES_ID_FOR_INTERVIEWER:
					List<String> activeId = new ArrayList<String>();
					String idInter = readString();
					if(interviewer != null){
						if(!interviewer.getId().equals(idInter)){	//ankieter mo¿e odebraæ tylko swoje ankiety
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
						System.out.println("No wysy³am no...");
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
						if(!interviewer.getId().equals(idInter2)){	//ankieter mo¿e odebraæ tylko swoje ankiety
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
								break;
							}
						}
						sendInt(AUTHORIZATION_OK);
						sendSurveyTemplate(surv);
						//sendString(gson.toJson(surv));
						//sendObject(surv);
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
		System.out.println("Wysy³am: " + i);	
		out.println(i);
	}
	
	private void sendObject(Object obj){
		try {
			
			ObjectOutputStream outObject = new ObjectOutputStream(incoming.getOutputStream());
			outObject.writeObject(obj);
			outObject.flush();
			System.out.println("Wysy³am obiekt " + obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendString(String s){
		System.out.println("Wysy³am: " + s);
		out.println(s);
	}
	
	private int readInt(){
		try{
			int i = Integer.parseInt(in.nextLine());
			System.out.println("Odczyta³em: " + i);
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
	
	
	private void sendSurveyTemplate(Survey survey){
		sendString(survey.getTitle());
		sendString(survey.getDescription());
		sendString(survey.getSummary());
		sendString(survey.getIdOfSurveys());
		Gson gson = new Gson();
		sendString(gson.toJson(survey.getInterviewer()));
		int i = survey.questionListSize();
		sendInt(i);
		for(int j = 0; j < i; j++){
			Question question = survey.getQuestion(j);
			int type = question.getQuestionType();
			sendInt(type);
			if(type == Question.TEXT_QUESTION){
				TextQuestion txt = (TextQuestion) question;
				sendString(txt.getErrorMessage());
				sendString(txt.getHint());
				sendString(txt.getPictureURL());
				sendString(txt.getQuestion());
				IConstraint constraint = txt.getConstraint();
				if(constraint != null){
					if(constraint instanceof TextConstraint){
						sendString("text");
						TextConstraint textConst = (TextConstraint) constraint;
						sendString((textConst.getMaxLength() == null)? "null" : "" + textConst.getMaxLength());
						sendString((textConst.getMinLength() == null)? "null" : "" + textConst.getMinLength());
						sendString((textConst.getRegex() == null)? "null" : textConst.getRegex().pattern());
					}
					else{
						sendString("number");
						NumberConstraint numbConst = (NumberConstraint) constraint;
						sendString((numbConst.getMaxValue() == null)? "null" : "" + numbConst.getMaxValue());
						sendString((numbConst.getMinValue() == null)? "null" : "" + numbConst.getMinValue());
						sendString((numbConst.getNotEquals() == null)? "null" : "" + numbConst.getNotEquals());
						sendString((numbConst.isMustBeInteger())? "true" : "false");
						sendString((numbConst.isNotBetweenMaxAndMinValue())? "true" : "false");
						System.out.println("Ograniczenie liczbowe min: " + ((((NumberConstraint) constraint).getMinValue() == null)? "null" : ((NumberConstraint) constraint).getMinValue()));
					}
				}
				else sendString("nothing"); //brak ograniczeñ
			}
			else{
				sendString(question.toJson());
			}
		}
	}
	
	private void sendFilledSurvey(Survey survey){
		sendSurveyTemplate(survey);
		
		sendInt(survey.getNumberOfSurvey());
	
		Gson gson = new Gson();
		sendString(gson.toJson(survey.getStartTime()));
		sendString(gson.toJson(survey.getFinishTime()));
		
		for(int i = 0; i < survey.questionListSize(); i++){
			List<String> answers = survey.getQuestion(i).getUserAnswersAsStringList();
			sendInt(answers.size());
			
			for(int j = 0; j < answers.size(); j++){
				sendString(answers.get(j));
			}
		}
	}
	
	private Survey receiveFilledSurvey(){
		Survey survey = receiveSurveyTemplate();
		int surveyNumber = readInt();
		survey.setNumberOfSurvey(surveyNumber);
		Gson gson = new Gson();
		String fromString = readString();
		System.out.println("Odczyta³em datê: " + fromString);
		GregorianCalendar from = gson.fromJson(fromString, GregorianCalendar.class);
		GregorianCalendar to = gson.fromJson(readString(), GregorianCalendar.class);
		survey.setStartTime(from);
		survey.setFinishTime(to);
		
		for(int i = 0; i < survey.questionListSize(); i++){
			int amount = readInt();
			System.out.println("W pytaniu: " + i + " mam " + amount + " odpowiedzi");
			List<String> answers = new ArrayList<String>(amount);
			for(int j = 0; j < amount; j++){
				answers.add(readString());
			}
			survey.getQuestion(i).setUserAnswers(answers);
		}
		return survey;
	}
	
	private Survey receiveSurveyTemplate(){
		Survey survey = new Survey(null);
		String read1;
		survey.setTitle(((read1 = readString()) == null)? null : read1);
		survey.setDescription(((read1 = readString()) == null)? null : read1);
		survey.setSummary(((read1 = readString()) == null)? null : read1);
		survey.setIdOfSurveys(readString());
		Gson gson = new Gson();
		survey.setInterviewer(gson.fromJson(readString(), Interviewer.class));
	
		int i = readInt();
		for(int j = 0; j < i; j++){
			int type = readInt();
			Question question;
			if(type == Question.DATE_QUESTION || type == Question.TIME_QUESTION){
				question = gson.fromJson(readString(), DateTimeQuestion.class);
			}
			else if(type == Question.DROP_DOWN_QUESTION || type == Question.ONE_CHOICE_QUESTION){
				question = gson.fromJson(readString(), OneChoiceQuestion.class);
			}
			else if(type == Question.GRID_QUESTION){
				question = gson.fromJson(readString(), GridQuestion.class);
			}
			else if(type == Question.MULTIPLE_CHOICE_QUESTION){
				question = gson.fromJson(readString(), MultipleChoiceQuestion.class);
			}
			else if(type == Question.SCALE_QUESTION){
				question = gson.fromJson(readString(), ScaleQuestion.class);
			}
			else{
				TextQuestion txt = new TextQuestion();
				txt.setErrorMessage(readString());
				txt.setHint(readString());
				txt.setPictureURL(readString());
				txt.setQuestion(readString());
				IConstraint constraint;
				String s = readString();
				if(s.equals("text")){
					String read;
					Integer maxLength = ((read = readString()).equals("null"))? null : Integer.parseInt(read);
					Integer minLength = ((read = readString()).equals("null"))? null : Integer.parseInt(read);
					String regexS = ((read = readString()).equals("null"))? null : read;
					Pattern regex = null;
					if(regexS != null)
						regex = Pattern.compile(regexS);
					constraint = new TextConstraint(minLength, maxLength, regex);
				}
				else if(s.equals("number")){
					String read;
					Double maxValue = ((read = readString()).equals("null"))? null : Double.parseDouble(read);
					Double minValue = ((read = readString()).equals("null"))? null : Double.parseDouble(read);
					Double notEquals = ((read = readString()).equals("null"))? null : Double.parseDouble(read);
					boolean mustBeInteger = Boolean.valueOf(readString());
					boolean notBetweenMaxAndMinValue = Boolean.valueOf(readString());
					constraint = new NumberConstraint(minValue, maxValue, mustBeInteger, notEquals, notBetweenMaxAndMinValue);
				}
				else constraint = null;
				txt.setConstraint(constraint);
				question = txt;
			}
			survey.addQuestion(question);
		}
		return survey;
	}
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		String s = gson.toJson(new Interviewer("dd", "de", "124", new GregorianCalendar()));
		System.out.println(s);
		Interviewer i = gson.fromJson(s, Interviewer.class);
		System.out.println(i);
	}
}
