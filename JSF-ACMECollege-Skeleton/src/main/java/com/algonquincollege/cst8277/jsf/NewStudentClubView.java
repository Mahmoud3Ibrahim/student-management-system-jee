/********************************************************************************************************
 * File:  NewStudentClubView.java
 * Course Materials CST 8277
 * 
 * @author Mike Norman
 * @author Teddy Yap
 * 
 */
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

import com.algonquincollege.cst8277.entity.StudentClub;
import com.algonquincollege.cst8277.entity.Academic;
import com.algonquincollege.cst8277.entity.NonAcademic;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("newStudentClub")
@ViewScoped
public class NewStudentClubView implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String desc;
    protected boolean isAcademic;
    
    @Inject
    @ManagedProperty("#{studentClubController}")
    protected StudentClubController studentClubController;

    public NewStudentClubView() {
    }
    
    /**
     * @return  name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name  name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  desc
     */
    public String getDesc() {
        return desc;
    }
    
    /**
     * @param desc  desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return  isAcademic
     */
    public boolean isAcademic() {
        return isAcademic;
    }
    
    /**
     * @param isAcademic  isAcademic to set
     */
    public void setAcademic(boolean isAcademic) {
        this.isAcademic = isAcademic;
    }

    public void addClub() {
        StudentClub theNewClub;
        if (isAcademic) {
            theNewClub = new Academic();
        } else {
            theNewClub = new NonAcademic();
        }
        theNewClub.setName(getName());
        theNewClub.setDesc(getDesc());
        studentClubController.addNewClub(theNewClub);
        
        //clean up
		studentClubController.toggleAdding();
        setName(null);
        setDesc(null);
        setAcademic(false);
    }
    
}

