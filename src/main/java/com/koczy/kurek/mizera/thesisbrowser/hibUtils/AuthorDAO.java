package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import org.hibernate.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
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

        session.close();

        if (authors.size() > 0) {
            resultAuthor = authors.get(0);
            Hibernate.initialize(resultAuthor.getTheses());
        } else {
            logger.log(Level.SEVERE,
                    "AuthorDAO.getAuthorById | Author not found. Returning null.");
        }

        return resultAuthor;
    }

    @Override
    public void saveAuthor(Author author) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.saveOrUpdate(author);
            transaction.commit();
        } catch (NonUniqueObjectException | EntityExistsException e) {
            logger.log(Level.INFO, "Thesis already connected with author.");
        }
        session.close();
    }

    @Override
    public Author getAuthorByName(String filterName) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Author> authors;
        Author resultAuthor = null;

        String sqlQuery = "SELECT * FROM author WHERE name LIKE :name";
        authors = session.createNativeQuery(sqlQuery, Author.class).setParameter("name", "%" + filterName + "%").list();

        if (authors.size() > 0) {
            resultAuthor = authors.get(0);
            Hibernate.initialize(resultAuthor.getTheses());
        }
        session.close();

        return resultAuthor;
    }
}
