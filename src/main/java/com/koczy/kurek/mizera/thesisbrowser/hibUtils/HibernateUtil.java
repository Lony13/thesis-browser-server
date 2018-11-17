package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    static {
        Configuration conf = new Configuration();
        conf.configure();
        try {
            sessionFactory = conf.buildSessionFactory();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Initial SessionFactory creation failed");
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
