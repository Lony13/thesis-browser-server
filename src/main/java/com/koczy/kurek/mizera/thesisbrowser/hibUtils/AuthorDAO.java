package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Logger;

@Repository
public class AuthorDAO implements IAuthorDao {
    private static final Logger logger = Logger.getLogger(AuthorDAO.class.getName());

    @Override
    public Author getAuthorByName(String filterName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Author> authors;
        Author resultAuthor = null;

        String sqlQuery = "SELECT * FROM author WHERE name LIKE '%" + filterName + "%'";
        authors = session.createNativeQuery(sqlQuery, Author.class).list();

        if (authors.size() > 0) {
            resultAuthor = authors.get(0);
            Hibernate.initialize(resultAuthor.getTheses());
        }
        session.close();

        return resultAuthor;
    }
}
