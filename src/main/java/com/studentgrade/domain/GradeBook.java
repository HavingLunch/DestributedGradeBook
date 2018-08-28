//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.23 at 01:31:53 PM EDT 
//
package com.studentgrade.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@Entity
@Table(name = "GradeBook")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "gradeBook")
public class GradeBook {

    @Column(name = "id")
    @Id
    @XmlElement(required = true)
    protected String id;
    @Column(name = "title")
    @XmlElement(required = true)
    protected String title;
    @Column(name = "serverType")
    @XmlElement(required = false)
    protected String serverType;
    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }
    /**
     * Gets the value of the ServerType property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getServerType() {
        return serverType;
    }

    /**
     * Sets the value of the ServerType property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setServerType(String value) {
        this.serverType = value;
    }
}


