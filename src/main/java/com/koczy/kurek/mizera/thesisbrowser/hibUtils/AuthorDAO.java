package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import org.hibernate.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class AuthorDAO implements IAuthorDao {
    private static final Logger logger = Logger.getLogger(AuthorDAO.class.getName());

    @Override
    public Author getAuthorById(int id) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Author> authors;
        Author resultAuthor = null;

        String sqlQuery = "SELECT * FROM author WHERE authorId = :id";
        authors = session.createNativeQuery(sqlQuery, Author.class).setParameter("id", id).list();

        if (!authors.isEmpty()) {
            resultAuthor = authors.get(0);
            Hibernate.initialize(resultAuthor.getTheses());
        } else {
            logger.log(Level.INFO,
                    "getAuthorById | Author with id: {0} not found.", id);
        }

        session.close();

        return resultAuthor;
    }

    @Override
    public Author getAuthorByName(String filterName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Author> authors;
        Author resultAuthor = null;

        String sqlQuery = "SELECT * FROM author WHERE name LIKE :name";
        authors = session.createNativeQuery(sqlQuery, Author.class).setParameter("name", "%" + filterName + "%").list();

        if (!authors.isEmpty()) {
            resultAuthor = authors.get(0);
            Hibernate.initialize(resultAuthor.getTheses());
        } else {
            logger.log(Level.INFO,
                    "getAuthorByName | Author: {0} not found.", filterName);
        }

        session.close();

        return resultAuthor;
    }

    public void saveAuthor(Author author) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.saveOrUpdate(author);
            transaction.commit();
        } catch (NonUniqueObjectException | EntityExistsException e) {
            logger.log(Level.INFO, "Thesis already connected with author: {0}.", author.getName());
        }
        session.close();
    }

    @Override
    public int getAuthorsNum() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String query = "SELECT count(1) FROM author";
        int result = ((BigInteger) session.createNativeQuery(query).uniqueResult()).intValue();
        session.close();

        return result;
    }
}
