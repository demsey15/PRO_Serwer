/**
 * 
 */
package bohonos.demski.mieldzioc.repositories;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import bohonos.demski.mieldzioc.interviewer.Interviewer;

/**
 * @author Dominik Demski
 *
 */
public class Workers {
	ConcurrentHashMap<Interviewer, String> interviewers = new ConcurrentHashMap<Interviewer, String>();
	ConcurrentHashMap<String, String> administartors = new ConcurrentHashMap<String, String>();
	
	public Workers(){
		Interviewer interviewer = new Interviewer("", "", "12345678999", new GregorianCalendar());
		interviewer.setInterviewerPrivileges(true);
		interviewers.put(interviewer, "abc");
		
		Interviewer interviewer2 = new Interviewer("", "", "11111111111", new GregorianCalendar());
		interviewer2.setInterviewerPrivileges(false);
		interviewers.put(interviewer2, "abc");
		administartors.put("admin", "admin");
	}
	
	public boolean chceckPassword(String id, char[] password){
		char[] rightPassword = null;
		if(administartors.contains(id)){
			rightPassword = administartors.get(id).toCharArray();
		}
		else{
			boolean goOn = false;
			int i = 0;
			Enumeration<Interviewer> enumeration = interviewers.keys();
			while(! goOn && i < interviewers.size()){
				Interviewer interviewer;
				if((interviewer = enumeration.nextElement()).getId().equals(id)){
					goOn = true;
					rightPassword = interviewers.get(interviewer).toCharArray();
				}
				i++;
			}
		}
		if(rightPassword == null) return false;    //brak u¿ytkownika o podanym id
		if(rightPassword.length != password.length){
			for(int i = 0; i < rightPassword.length; i++){
				rightPassword[i] = 'a';
			}
			return false;
		}
		for(int i = 0; i < rightPassword.length; i++){
			if(rightPassword[i] != password[i]){
				for(int j = 0; j < rightPassword.length; j++){
					rightPassword[j] = 'a';
					password[j] = 'a';
				}
				return false;
			}
		}
		for(int j = 0; j < rightPassword.length; j++){
			rightPassword[j] = 'a';
			password[j] = 'a';
		}
		return true;
	}
	
	/**
	 * Zwraca ankietera o zadanym id, jeœli nie ma takiego, zwraca null.
	 * @param id id nakietera.
	 * @return ankietera o zadanym id, jeœli nie ma takiego, zwraca null.
	 */
	public Interviewer getInterviewer(String id){
		for(Interviewer interviewer : interviewers.keySet()){
			if(interviewer.getId().equals(id)) return interviewer;
		}
		return null;
	}
	
	public boolean addNewAdministrator(String id, char[] password){
		if(administartors.contains(id)) return false;
		else{
			administartors.put(id, new String(password));
			for(int j = 0; j < password.length; j++){
				password[j] = 'a';
			}
			return true;
		}
	}

	public boolean addNewInterviewer(Interviewer interviewer, char[] password){
		if(interviewers.contains(interviewer)) return false;
		else{
			interviewers.put(interviewer, new String(password));
			for(int j = 0; j < password.length; j++){
				password[j] = 'a';
			}
			return true;
		}
	}
	
	public List<Interviewer> getAllInterviewers(){
		List<Interviewer> list = new ArrayList<Interviewer>(interviewers.size());
		for(Interviewer interv : interviewers.keySet()){
			list.add(interv);
		}
		return list;
	}
}
