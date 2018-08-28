package com.studentgrade.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.studentgrade.domain.GradeBook;
import com.studentgrade.domain.Server;
import com.studentgrade.model.ServerHandler;

/**
 *
 * @author fuguanglin
 */
@Path("")
public class ServerConfigurationService {
	ServerHandler serverHandler = new ServerHandler();

	@GET
	@Path("PrimaryServer")
	@Produces("application/xml")
	public Server getPrimaryServer() {
		return serverHandler.getPrimaryServer();
	}

	@POST
	@Path("PrimaryServer")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("text/plain")
	public String createAndUpdatePrimaryServer_POST(Server server) {
		server.setType("primary");
		serverHandler.setPrimaryServer(server);
		return "";
	}

	@GET
	@Path("SecondaryServer")
	@Produces("application/xml")
	public Server getSecondaryServer() {
		return serverHandler.getSecondaryServer();
	}

	@POST
	@Path("URLVerification")
	@Produces("text/plain")
	@Consumes("text/plain")
	public String isUrlValid(String url) {
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return "true";
			} else {
				return "false";
			}
		} catch (Exception e) {
			System.out.println("Exception");
			System.out.println(e.getMessage());
			return "false";
		}

	}

	@POST
	@Path("SecondaryServer")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces("text/plain")
	public String createAndUpdateSecondaryServer_POST(Server server) {
		server.setType("secondary");
		serverHandler.setSecondaryServer(server);
		return "";
	}

	@DELETE
	@Path("ClearAllGradeBookAndStudent")
	@Produces("text/plain")
	public String ClearAllGradeBookAndStudent(@Context UriInfo uriInfo) {
		GradeBookService gbs = new GradeBookService();
		try {
			List<GradeBook> gbl = gbs.getAllGradeBook().getGradeBook();
			if (gbl != null && gbl.size() > 0) {
				gbl.forEach(a -> gbs.deleteGradeBookbyID(a.getId()));
			}
		} catch (WebApplicationException e) {
			if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
				throw e;
			}
		}
		return "The server \"" + uriInfo.getBaseUri() + "\" has been cleaned!";
	}
}
