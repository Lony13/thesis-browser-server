package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class dbUtil {

    public static void saveAuthor(Author author) {
        save(author);
    }

    public static void saveThesis(Thesis thesis) {
        save(thesis);
    }

    private static void save(Object object) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.save(object);

        transaction.commit();
        session.close();
    }

    //TODO
    public static ArrayList<Thesis> getAllThesis() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Thesis> thesisList = session.createNativeQuery("SELECT * FROM thesis", Thesis.class).list();
        List<Author> authorList = session.createNativeQuery("SELECT * FROM author", Author.class).list();
        System.out.println("THESIS SIZE : " + thesisList.size());
        System.out.println("AUTHORS SIZE : " + authorList.size());

        for (Thesis t : thesisList) {
            System.out.println(t.getTitle());
            System.out.println("Authors : " + t.getAuthors());

            for (Author author : t.getAuthors()) {
//                if(author.getAuthorId() == t)
                System.out.println("Author : " + author.getLastName());
            }
        }
//
//        for (Author author : authorList) {
//            System.out.println(author.getLastName());
//            for (Thesis thesis : author.getTheses()) {
//                System.out.println("Title : " + thesis.getTitle());
//            }
//        }

        session.close();

        return (ArrayList<Thesis>) thesisList;

    }
}
