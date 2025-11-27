/********************************************************************************************************
 * File:  NonAcademicClub.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;

//TODO NA01 - Add missing annotations, please see lecture slides.  Value 1 is academic and value 0 is non-academic.
public class NonAcademic extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public NonAcademic() {
		super(false);
	}
}