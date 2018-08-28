package com.studentgrade.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="grade" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@Entity
@Table(name = "Student")
@IdClass(StudentPK.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "student")
public class Student implements Serializable{

    @Column(name = "name")
    @Id
    @XmlElement(required = true)
    protected String name;
    @Column(name = "gradeBookID")
    @Id
    @XmlElement(required = false)	
    protected String gradeBookID;    
    @Column(name = "grade")
    @XmlElement(required = true)
    protected String grade;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }
    /**
     * Gets the value of the gradeBookID property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getGradeBookID() {
        return gradeBookID;
    }

    /**
     * Sets the value of the gradeBookID property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setGradeBookID(String value) {
        this.gradeBookID = value;
    }
    /**
     * Gets the value of the grade property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setGrade(String value) {
        this.grade = value;
    }

}