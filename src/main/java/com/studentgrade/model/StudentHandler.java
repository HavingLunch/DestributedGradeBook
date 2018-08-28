package com.studentgrade.model;

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
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.studentgrade.domain.Student;
import com.studentgrade.domain.StudentList;

public class StudentHandler {

	public StudentList getAllStudent(String gradeBookId) {
		Session session = begainTransactions();
		List<Student> studentList = session.createQuery("FROM Student where gradeBookID = :gradeBookID")
				.setParameter("gradeBookID", gradeBookId).list();
		commitTransactions();
		// session.close();
		StudentList sdl = new StudentList();
		sdl.setStudent(studentList);
		return sdl;
	}

	public Student getStudentByName(String gradebookId, String name) {
		Session session = begainTransactions();
		List<Student> studentList = session
				.createQuery("FROM Student where gradeBookID = :gradeBookID and name = :name")
				.setParameter("gradeBookID", gradebookId).setParameter("name", name).list();
		commitTransactions();
		// session.close();
		if (studentList.isEmpty()) {
			return null;
		}
		return studentList.get(0);
	}

	public Boolean deleteStudent(Student stu) {
		Session session = begainTransactions();
		session.delete(stu);
		commitTransactions();
		// session.close();
		return true;
	}

	public Boolean deleteStudents(List<Student> stulist) {
		stulist.forEach(stu -> deleteStudent(stu));
		return true;
	}

	public Boolean isExist(Student stu) {
		Student student = getStudentByName(stu.getGradeBookID(), stu.getName());
		if (student == null) {
			return false;
		} else
			return true;

	}

	public String updateStudent(Student stu) {
		String reponceMassage;
		Session session;
		if (isExist(stu)) {
			session = begainTransactions();
			session.update(stu);
			reponceMassage = "GradeBook:" + stu.getGradeBookID() + "student:" + stu.getName() + " has been updated.";
		} else {
			session = begainTransactions();
			session.persist(stu);
			reponceMassage = "GradeBook:" + stu.getGradeBookID() + "student:" + stu.getName() + " has been added.";
		}
		commitTransactions();
		// session.close();
		return reponceMassage;
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
