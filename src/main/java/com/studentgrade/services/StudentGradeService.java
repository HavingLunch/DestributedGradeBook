/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.studentgrade.services;

import com.studentgrade.domain.*;
import com.studentgrade.model.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.util.regex.Pattern;

import javax.ws.rs.*;
import javax.ws.rs.core.*;


/**
 *
 * @author fuguanglin
 */
@Path("")
public class StudentGradeService {
	@Context
	UriInfo uri;
	StudentHandler sh = new StudentHandler();
	ServerHandler serverHandler = new ServerHandler();
	GradeBookHandler gbh = new GradeBookHandler();
	
	@GET
	@Path("{id}/student")
	@Produces("application/xml")
	public StudentList getAllStudent(@PathParam("id") String gradeBookId) {
		StudentList studentList = sh.getAllStudent(gradeBookId);
		if (studentList.getStudent().isEmpty()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return studentList;
	}

	@GET
	@Path("{id}/student/{name}")
	@Produces("application/xml")
	public Student getStudentByName(@PathParam("id") String gradeBookId, @PathParam("name") String name) {
		Student std = sh.getStudentByName(gradeBookId, name);
		if (std == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return std;
	}

	@DELETE
	@Path("{id}/student/{name}")
	@Produces("text/plain")
	public String deleteStudentByName(@PathParam("id") String gradeBookId, @PathParam("name") String name) {
		String result; 
		Student std = sh.getStudentByName(gradeBookId, name);
		GradeBook gb = gbh.getGradeBookById(gradeBookId);
		if (std == null || gb == null) {
			return "GradeBookId:" + gradeBookId + " student:" + name + " doesn't exist on this machine.";
		} else if (gb.getServerType().equals("secondary")) {
			return "GradeBookId:" + gradeBookId + " student:" + name + " doesn't belong to this machine.";
		}
		sh.deleteStudent(std);
		result = "student:" + std.getName() + " has been deleted.";
		result = result + "\n" + deleteStudentGradeRemote(std);
		return result;
	}

	@PUT
	@Path("{id}/student/{name}/grade/{letter}")
	@Produces("text/plain")
	public String createAndUpdateStudentGrade_Put(@PathParam("id") String gradeBookId, @PathParam("name") String name, @PathParam("letter") String letter) {
		System.out.println("createAndUpdateStudentGrade_Put ran");
		return createAndUpdateStudentGrade(gradeBookId, name, letter);
	}

	@POST
	@Path("{id}/student/{name}/grade/{letter}")
	@Produces("text/plain")
	public String createAndUpdateStudentGrade_Post(@PathParam("id") String gradeBookId, @PathParam("name") String name, @PathParam("letter") String letter) {

		System.out.println("createAndUpdateStudentGrade_Post ran");
		return createAndUpdateStudentGrade(gradeBookId, name, letter);
	}

	private String createAndUpdateStudentGrade(String gradeBookId, String name, String letter) {
		Pattern pattern = Pattern.compile("(?i)[ABCDEFIW]|([ABCD][+-])");
		if (!pattern.matcher(letter).matches()) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		System.out.println(gradeBookId + "  " +  name + "  " +  letter);
		Student student = new Student();
		student.setGradeBookID(gradeBookId);
		student.setName(name);
		student.setGrade(letter);
		String result;
		GradeBook gb = gbh.getGradeBookById(student.getGradeBookID());
		if(gb == null || (gb != null && gb.getServerType().equals("secondary"))) {
				return "GradeBookID:" + student.getGradeBookID() + 
						"StudentName:" + student.getName() +
						" belongs to the remote Machine or doesn't exist. Checked locally";			
		} 
		result = sh.updateStudent(student);
		result = result + "\n" + updateStudentGradeRemote(student);
		return result;
	}
	
	private String updateStudentGradeRemote(Student stu) {
		Client client = Client.create();
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);
		String result = "";
		try {
			result = resource.path("secondary/updateStudentGrade").accept(MediaType.TEXT_PLAIN).post(String.class, stu);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				e.printStackTrace();
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		return result;
	}
	private String deleteStudentGradeRemote(Student stu) {
		Client client = Client.create();
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);
		String result = "";
		try {
			result = resource.path("secondary/deleteStudentGrade").accept(MediaType.TEXT_PLAIN).delete(String.class, stu);
		//  result = resource.path("secondary/updateStudentGrade").accept(MediaType.TEXT_PLAIN).post(String.class, stu);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				e.printStackTrace();
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		return result;
	}
}
