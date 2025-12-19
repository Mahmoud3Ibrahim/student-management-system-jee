
package com.algonquincollege.cst8277.jsf;

import java.io.Serializable;

public class ClubMembershipRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int studentId;
    private int clubId;

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

