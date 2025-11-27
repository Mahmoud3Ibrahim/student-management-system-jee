/********************************************************************************************************
 * File:  AcademicClub.java Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package com.algonquincollege.cst8277.entity;

import java.io.Serializable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//TODO A01 - Add missing annotations, please see lecture slides.  Value 1 is academic and value 0 is non-academic.
@Entity
@DiscriminatorValue("1")
public class Academic extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public Academic() {
		super(true);
	}
}
