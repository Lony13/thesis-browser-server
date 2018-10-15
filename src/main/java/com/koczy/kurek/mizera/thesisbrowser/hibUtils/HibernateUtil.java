package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
//    private static final ServiceRegistry serviceRegistry;

    static {
        Configuration conf = new Configuration();
        conf.configure();
//        serviceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
        try {
//            sessionFactory = conf.buildSessionFactory(serviceRegistry);
            sessionFactory = conf.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
