/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.studentgrade.services;

import com.studentgrade.domain.*;
import com.studentgrade.model.*;

import java.util.Comparator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author fuguanglin
 */
@Path("")
public class GradeBookService {

	GradeBookHandler gbh = new GradeBookHandler();
	ServerHandler serverHandler = new ServerHandler();
	StudentHandler sh = new StudentHandler();
	
	@GET
	@Produces("application/xml")
	public GradeBookList getAllGradeBook() {
		// get GradeBook From current Machine
		GradeBookList gbl = gbh.getAllGradeBook();
		// get GradeBook From the remote Machine
		GradeBookList gbl_remote = getAllGradeBookRemote();
		if(gbl_remote != null) {
			gbl.getGradeBook().addAll(gbl_remote.getGradeBook());
		}
		if (gbl.getGradeBook().isEmpty()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return gbl;
	}

	private GradeBookList getAllGradeBookRemote() {
		Client client = Client.create();
		GradeBookList gbl = null;
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);

		try {
			gbl = resource.path("secondary").get(GradeBookList.class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		return gbl;
	}

	private GradeBook getGradeBookbyTitleRemote(String title) {
		Client client = Client.create();
		GradeBook gb = null;
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);
		try {
			gb = resource.path("secondary/getGradeBookbyTitle/" + title).get(GradeBook.class);
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
		
		return gb;
	}
	private void deleteGradeBookRemote(GradeBook gb) {
		Client client = Client.create();
		String url = serverHandler.getSecondaryServer().getUrl();
		WebResource resource = client.resource(url);
		String result = "";
		try {
			resource.path("secondary/" + gb.getId())
					.accept(MediaType.TEXT_PLAIN).delete();
		} catch (UniformInterfaceException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				e.printStackTrace();
				//throw new WebApplicationException(Response.Status.EXPECTATION_FAILED);
			}
		}
	}
	
	@DELETE
	@Path("{id}")
	@Produces("text/plain")
	public String deleteGradeBookbyID(@PathParam("id") String id) {
		GradeBook gb = gbh.getGradeBookById(id);
		if (gb == null) {
			return "GradeBookID:" + id + " doesn't exist.";
		}
		if (gb.getServerType().equals("secondary")) {
			return "GradeBookID:" + id + " doesn't belong to this machine.";
		}
		gbh.deleteGradeBook(gb);
		//DeleteRelatedStudent
		StudentList studentList = sh.getAllStudent(id);
		sh.deleteStudents(studentList.getStudent());
		deleteGradeBookRemote(gb);
		return "GradeBook ID:" + gb.getId() + "Title:" + gb.getTitle() + " has been deleted.";
	}

	@PUT
	@Path("{name}")
	@Produces("text/plain")
	public String createGradeBook_Put(@PathParam("name") String name) {
		return createGradeBook(name);
	}

	@POST
	@Path("{name}")
	@Produces("text/plain")
	public String createGradeBook_Post(@PathParam("name") String name) {
		return createGradeBook(name);
	}

	private String createGradeBook(String name) {
		GradeBook gb = gbh.getGradeBookByTitle(name);
		System.out.println("------" + name + "-----");
		if (gb != null) {
			System.out.println("------check1 local-----");
			return "GradeBook:" + name + " exists already.";			
		} else {
			gb = getGradeBookbyTitleRemote(name);
			if (gb != null) {
				System.out.println("------check remote-----");
				return "GradeBook:" + name + " exists already on the remote machine.";
			} 			
		}
		gb = new GradeBook();
		// set grade book ID
		int maxID = 0;
		// get GradeBook From current Machine
		System.out.println("------Get All GradeBook before-----");
		GradeBookList gbl = gbh.getAllGradeBook();
		System.out.println("------Get All GradeBook after-----");
		System.out.println(gbl.getGradeBook());
		// get GradeBook From the remote Machine
		System.out.println("------Get All GradeBookRemote before-----");
		GradeBookList gbl_remote = getAllGradeBookRemote();
		System.out.println("-----getAllGradeBookRemote After------");
		if(gbl_remote != null) {
			gbl.getGradeBook().addAll(gbl_remote.getGradeBook());
		}
		if (gbl != null && gbl.getGradeBook().size() > 0) {
			gbl.getGradeBook().sort(new SortbyID());
			maxID = Integer.valueOf(gbl.getGradeBook().get(0).getId()) + 1;
		}
		gb.setId(String.valueOf(maxID));
		gb.setTitle(name);
		gb.setServerType("primary");
		gbh.updateGradeBook(gb);
		return "GradeBook ID:" + gb.getId() + "Title:" + gb.getTitle() + " has been created.";
	}

	class SortbyID implements Comparator<GradeBook> {
		// Used for sorting in ascending order of
		// roll name
		public int compare(GradeBook a, GradeBook b) {
			return Integer.valueOf(b.getId()).compareTo(Integer.valueOf(a.getId()));
		}
	}
}
