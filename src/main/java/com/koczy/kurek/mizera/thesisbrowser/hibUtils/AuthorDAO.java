package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AuthorDAO {
    private static final Logger logger = Logger.getLogger(AuthorDAO.class.getName());

    public static Author getAuthorByName(String filterName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Author> authors = new ArrayList<>();

        String sqlQuery = "SELECT * FROM author WHERE name LIKE '%" + filterName + "%'";
        authors = session.createNativeQuery(sqlQuery, Author.class).list();
        Author resultAuthor = authors.get(0);
        Hibernate.initialize(resultAuthor.getTheses());

        session.close();

        return resultAuthor;
    }
}
