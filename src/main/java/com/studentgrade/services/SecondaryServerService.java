/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.studentgrade.services;

import com.studentgrade.domain.*;
import com.studentgrade.model.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 * @author fuguanglin
 */
@Path("")
public class SecondaryServerService {
	ServerHandler serverHandler = new ServerHandler();
    GradeBookHandler gbh = new GradeBookHandler();
    StudentHandler sh = new StudentHandler();
    
    @GET
    @Produces("application/xml")
    public GradeBookList getAllGradeBook() {
    	System.out.println("getAllGradeBook is called remotely!!!");
    	GradeBookList gbl = gbh.getAllGradeBook();
    	if (gbl.getGradeBook().isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    	if(gbl != null && gbl.getGradeBook() != null) {
    		gbl.getGradeBook().forEach(a->a.setServerType("secondary"));
    	}
        return gbl;
    }
    
	@GET
	@Path("{id}")
	@Produces("application/xml")
    public GradeBook getGradeBookByID(@PathParam("id") String id) {
    	GradeBook gb = gbh.getGradeBookById(id);
    	if (gb == null || !gb.getServerType().equals("primary")) {
    		if(gb == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return gb;
    }

	@GET
	@Path("getGradeBookbyTitle/{title}")
	@Produces("application/xml")
    public GradeBook getGradeBookbyTitle(@PathParam("title") String title) {
    	GradeBook gb = gbh.getGradeBookByTitle(title);
    	if(gb == null) throw new WebApplicationException(Response.Status.NOT_FOUND);
        return gb;
    }
	
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
	
	@DELETE
	@Path("{id}")
	@Produces("text/plain")
	public String deleteGradeBookbyID(@PathParam("id") String id) {
		GradeBook gb = gbh.getGradeBookById(id);
		if (gb == null) {
			return "GradeBookID:" + id + " doesn't exist.";
		}
		if (gb.getServerType().equals("primary") ) {
			return "GradeBookID:" + id + " belong to this machine.";
		}
		gbh.deleteGradeBook(gb);
		//DeleteRelatedStudent
		StudentList studentList = sh.getAllStudent(id);
		sh.deleteStudents(studentList.getStudent());
		return "GradeBook ID:" + gb.getId() + "Title:" + gb.getTitle() + " has been deleted.";
	}

	@PUT
	@Path("{id}")
	@Produces("text/plain")
	public String createGradeBook_Put(@PathParam("id") String id) {
		return copyGradeBook(id);
	}

	@POST
	@Path("{id}")
	@Produces("text/plain")
	public String createGradeBook_Post(@PathParam("id") String id) {
		return copyGradeBook(id);
	}
	
	private String copyGradeBook(String id) {
		GradeBook gb = gbh.getGradeBookById(id);
		if (gb != null) {
			return "GradeBookID:" + id + " exists already on this Machine.";
		}
		gb = getGradeBookRemoteByID(id);
		if (gb == null) {
			return "GradeBookID:" + id + " doesn't exists already on remote Machine.";
		}	
		System.out.println("GradeBookID:" + gb.getId() + " Name:" + gb.getTitle() +" Remote");
		gb.setServerType("secondary");
		gbh.updateGradeBook(gb);
		
		//copy Student List
		StudentList sl = getAllStudentByGradeBookId(id);
		if(sl != null && sl.getStudent() != null) {
			sl.getStudent().forEach(a->sh.updateStudent(a));
		}
		return "GradeBookID:" + id + " has been copied from the remote Machine.";
	}
	
	@DELETE
	@Path("deleteStudentGrade")
	@Produces("text/plain")
	@Consumes(MediaType.APPLICATION_XML)
	public String deleteStudentGradebyID(Student stu) {
		System.out.println("----------" + "deleteStudentGradebyID -----Start");
		GradeBook gradeBook = gbh.getGradeBookById(stu.getGradeBookID());
		Student originalStudent = sh.getStudentByName(stu.getGradeBookID(), stu.getName());
		if(originalStudent ==null || gradeBook == null) {
			return "GradeBookID:" + stu.getGradeBookID() + 
					"StudentName:" + stu.getName() +
					" doesn't exist on remote Machine.";
		} else if (gradeBook.getServerType().equals("primary")){
			return "GradeBookID:" + stu.getGradeBookID() + 
					"StudentName:" + stu.getName() +
					" belongs to the remote Machine.";
		} else {
			System.out.println("----------" + "sh.deleteStudent(originalStudent);");
			sh.deleteStudent(originalStudent);
		}
		return "GradeBookID:" + stu.getGradeBookID() + 
				"StudentName:" + stu.getName() +
				" has been deleted on remote Machine.";
	}
	
	@PUT
	@Path("updateStudentGrade")
	@Produces("text/plain")
	@Consumes(MediaType.APPLICATION_XML)
	public String updateStudentGrade_Put(Student stu) {
		return updateStudentGrade(stu);
	}

	@POST
	@Path("updateStudentGrade")
	@Produces("text/plain")
	@Consumes(MediaType.APPLICATION_XML)
	public String updateStudentGrade_Post(Student stu) {
		return updateStudentGrade(stu);
	}
	private String updateStudentGrade(Student stu) {
		GradeBook gradeBook = gbh.getGradeBookById(stu.getGradeBookID());
		Student originalStudent = sh.getStudentByName(stu.getGradeBookID(), stu.getName());
		if(gradeBook == null) {
			return "GradeBookID:" + stu.getGradeBookID() + 
					"StudentName:" + stu.getName() +
					" doesn't exists on remote Machine.";
		} else if (gradeBook.getServerType().equals("primary")){
			return "GradeBookID:" + stu.getGradeBookID() + 
					"StudentName:" + stu.getName() +
					" belongs to the remote Machine. Checked locally";
		} else {
			sh.updateStudent(stu);
		}
		return "GradeBookID:" + stu.getGradeBookID() + 
				"StudentName:" + stu.getName() +
				" has been synchronized on remote Machine.";	
	}
	
	private GradeBook getGradeBookRemoteByID(String id) {
		Client client = Client.create();
		GradeBook gb = null;
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);
		System.out.println("GradeBookID:" + id  +" getGradeBookRemoteByID start!!!!");
		try {
			gb = resource.path("secondary/" + id).get(GradeBook.class);
		} catch (UniformInterfaceException e) {
			System.out.println("GradeBookID:" + id  + " Responce From remote" + e.getResponse().getStatus() +" Remote failed !!!!!!!!!!");
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		return gb;
	}
	private StudentList getAllStudentByGradeBookId(String gradeBookId) {
		Client client = Client.create();
		StudentList sl = null;
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);

		try {
			sl = resource.path("secondary/" + gradeBookId + "/student").get(StudentList.class);
		} catch (UniformInterfaceException e) {
			System.out.println("GradeBookID:" + gradeBookId  + " Responce From remote" + e.getResponse().getStatus() +" Remote failed !!!!!!!!!!");
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		return sl;
	}

}
