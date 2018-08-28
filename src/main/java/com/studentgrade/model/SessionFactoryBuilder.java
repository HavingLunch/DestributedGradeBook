package com.studentgrade.model;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SessionFactoryBuilder {
	private static SessionFactory sessionFactory;
	
	public static SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			Configuration cfg = new Configuration();
			try {
				cfg.configure("hibernate.cfg.xml");
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties())
						.build();
				sessionFactory = cfg.buildSessionFactory(serviceRegistry);
				System.out.println("This server is using hibernate.cfg.xml for Database setting");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return sessionFactory;
	}
}
