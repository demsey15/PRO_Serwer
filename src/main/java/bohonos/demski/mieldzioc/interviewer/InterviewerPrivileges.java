/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bohonos.demski.mieldzioc.interviewer;

import java.io.Serializable;

/**
 *
 * @author Delirus
 */
public class InterviewerPrivileges implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean creating = false;
    public void changePrivileges(boolean creating){
        this.creating=creating;
    }
    public boolean getCreating(){
        return creating;
    }
}
