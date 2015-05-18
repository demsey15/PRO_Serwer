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
public class InterviewerSurveyPrivileges implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean editing, filling, editingWithoutAdminAgreement, fillingStatistics;
    
     public InterviewerSurveyPrivileges(boolean editing, boolean filling, boolean editingWithoutAdminAgreement, boolean fillingStatistics){
        this.editing= editing;
        this.filling = filling;
        this.editingWithoutAdminAgreement = editingWithoutAdminAgreement;
        this.fillingStatistics = fillingStatistics;
    }
     
    public void setEditing(boolean editing){
        this.editing= editing;
    }
    public void setFilling(boolean filling){
        this.filling= filling;
    }
    public void setEditingWithoutAdminAgreement(boolean editingWithoutAdminAgreement){
        this.editingWithoutAdminAgreement= editingWithoutAdminAgreement;
    }
    public void setFillingStatistics(boolean fillingStatistics){
        this.fillingStatistics= fillingStatistics;
    }
    
    public boolean isEditing() {
        return editing;
    }

    public boolean isFilling() {
        return filling;
    }

    public boolean isEditingWithoutAdminAgreement() {
        return editingWithoutAdminAgreement;
    }

    public boolean isFillingStatistics() {
        return fillingStatistics;
    }
}
