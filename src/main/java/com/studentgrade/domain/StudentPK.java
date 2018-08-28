package com.studentgrade.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

public class StudentPK implements Serializable {
	protected String name;
	protected String gradeBookID; 
	public StudentPK() {}
	public StudentPK(String name, String gradeBookID) {
        this.name = name;
        this.gradeBookID = gradeBookID;
    }

    
}
