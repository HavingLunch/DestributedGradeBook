package com.studentgrade.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.studentgrade.domain.Server;
import com.studentgrade.domain.Student;
import com.studentgrade.domain.StudentList;

public class ServerHandler {
	public Server getPrimaryServer() {
		Session session = begainTransactions();
		List serverList = session.createQuery("FROM Server where type = 'primary'").list();
		commitTransactions();
		//session.close();
		if(serverList.size()>0) {
			return (Server)serverList.get(0);
		} else return null;
	}
	
	public void setPrimaryServer(Server server) {
		Session session = begainTransactions();
		session.delete(server);
		commitTransactions();
		System.out.println("setPrimaryServer delete");
		session = begainTransactions();
		session.persist(server);
		System.out.println("setPrimaryServer persist");
		commitTransactions();
		//session.close();
	}
	public Server getSecondaryServer() {
		System.out.println(" getSecondaryServer start!!!!");
		Session session = begainTransactions();
		List serverList = session.createQuery("FROM Server where type = 'secondary'").list();
		commitTransactions();
		//session.close();
		if(serverList.size()>0) {
			return (Server)serverList.get(0);
		} else {
			return null;
		}
	}	
	public void setSecondaryServer(Server server) {
		Session session = begainTransactions();
		session.delete(server);
		commitTransactions();
		session = begainTransactions();
		System.out.println("setSecondaryServer delete");
		session.persist(server);
		System.out.println("setSecondaryServer persist");
		commitTransactions();
		//session.close();
	}
	private void commitTransactions() {
		Session session = SessionFactoryBuilder.getSessionFactory().getCurrentSession();
		try {			
			session.getTransaction().commit();
		} catch (Exception ex) {
			try {
				session.getTransaction().rollback();
			} catch (Exception e) {}
			System.out.println("Transaction Commit failed!!!!!!!!!!!!!!!!!");
		}
	}

	private Session begainTransactions() {
		Session session = SessionFactoryBuilder.getSessionFactory().getCurrentSession();

		for (int i = 0; i < 30; i++) {
			try {
				if (!session.getTransaction().isActive()) {
					try {
						session.beginTransaction();
						return session;
					} catch (Exception e) {

					}
				} else {
					session.getTransaction().commit();
				}
			} catch (Exception ex) {
				session = SessionFactoryBuilder.getSessionFactory().openSession();
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		session = SessionFactoryBuilder.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		return session;

	}
}
