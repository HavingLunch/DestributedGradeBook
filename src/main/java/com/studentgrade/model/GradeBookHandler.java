package com.studentgrade.model;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.studentgrade.domain.GradeBook;
import com.studentgrade.domain.GradeBookList;
import com.studentgrade.domain.Server;

public class GradeBookHandler {

	public GradeBookList getAllGradeBook() {
		Session session = begainTransactions();
		List<GradeBook> gradebookList = session.createQuery("FROM GradeBook where serverType = 'primary'").list();
		commitTransactions();
		GradeBookList gbl = new GradeBookList();
		gbl.setGradeBook(gradebookList);
		return gbl;
	}

	public GradeBook getGradeBookById(String Id) {
		Session session = begainTransactions();
		GradeBook gradeBook = (GradeBook) session.get(GradeBook.class, Id);
		commitTransactions();
		return gradeBook;
	}

	public GradeBook getGradeBookByTitle(String title) {
		Session session = begainTransactions();
		String hql = "FROM GradeBook where title = :title";
		List result = session.createQuery(hql).setParameter("title", title).list();
		commitTransactions();
		if (result.size() > 0) {
			return (GradeBook) result.get(0);
		} else {
			return null;
		}
	}

	public Boolean deleteGradeBook(GradeBook gb) {
		Session session = begainTransactions();
		session.delete(gb);
		commitTransactions();
		return true;
	}

	public Boolean validateGradeBook(GradeBook gb) {

		return true;
	}

	public Boolean isExist(GradeBook gb) {
		GradeBook gradebook = getGradeBookById(gb.getId());
		if (gradebook == null) {
			return false;
		} else
			return true;

	}

	public void updateGradeBook(GradeBook gb) {
		Session session;
		if (isExist(gb)) {
			session = begainTransactions();
			session.update(gb);
		} else {
			session = begainTransactions();
			session.persist(gb);
		}
		commitTransactions();
	}

	private void commitTransactions() {
		Session session = SessionFactoryBuilder.getSessionFactory().getCurrentSession();
		try {			
			session.getTransaction().commit();
		} catch (Exception ex) {
			try {
				session.getTransaction().rollback();
			} catch (Exception e) {}
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
				e.printStackTrace();
			}
		}
		session = SessionFactoryBuilder.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		return session;

	}
}
