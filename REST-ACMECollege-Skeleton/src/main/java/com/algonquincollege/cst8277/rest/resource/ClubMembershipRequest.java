/********************************************************************************************************
 * File:  ClubMembershipRequest.java Course Materials CST 8277
 *
 * This is an academic graduation project for CST8277 course.
 *
 * @author Teddy Yap
 * @author Mahmoud Ibrahim
 * 
 */
package com.algonquincollege.cst8277.rest.resource;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Simple payload for club membership actions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubMembershipRequest implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    protected int studentId;
    protected int clubId;

    public ClubMembershipRequest() {
        super();
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
}

