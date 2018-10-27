package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class ThesisDAO implements IThesisDao {

    private static final Logger logger = Logger.getLogger(ThesisDAO.class.getName());
    private static Boolean addAnd = false;

    private IAuthorDao authorDao;

    @Autowired
    public ThesisDAO(IAuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    //TODO add filter over position in authors list
    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Thesis> thesisList;

        try {
            String sqlQuery = createQuery(thesisFilters);
            thesisList = session.createNativeQuery(sqlQuery, Thesis.class).list();
            for (Thesis thesis : thesisList) {
                Hibernate.initialize(thesis.getRelatedTheses());
                Hibernate.initialize(thesis.getKeyWords());
                Hibernate.initialize(thesis.getAuthors());
            }
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "NullPointerException in thesisFilers. Returning empty list." + e.toString());
            thesisList = new ArrayList<>();
        }

        filterDate(thesisList, thesisFilters);

        session.close();

        return thesisList;
    }

    private void filterDate(List<Thesis> thesisList, ThesisFilters thesisFilters) {
        if (Objects.nonNull(thesisFilters.getDateFrom())) {
            thesisList.removeIf(thesis -> thesis.getPublicationDate().after(thesisFilters.getDateFrom()));
        }
        if (Objects.nonNull(thesisFilters.getDateTo())) {
            thesisList.removeIf(thesis -> thesis.getPublicationDate().after(thesisFilters.getDateTo()));
        }
    }

    private String createQuery(ThesisFilters filters) {

        String query = "SELECT * FROM thesis " +
                "JOIN author_thesis on thesis.thesisId = author_thesis.thesisId " +
                "JOIN author on author_thesis.authorId = author.authorId " +
                "JOIN keywords on keywords.thesisId = thesis.thesisId " +
                "WHERE ";

        query = filterTitle(query, filters.getTitle());
        query = filterAuthor(query, filters.getAuthor());
        query = filterInstitution(query, filters.getInstitution());
        query = filterKeyWords(query, filters.getKeyWords());
        query = filterQuotationNumber(query, filters.getQuotationNumber());

        return query;
    }

    private String filterTitle(String query, String title) {
        if (!isBlank(title)) {
            query = addAndToWhere(addAnd, query);
            query = query.concat("thesis.title LIKE '%" + title + "%' ");
            addAnd = true;
        }
        return query;
    }

    private String filterAuthor(String query, String authorName) {
        if (!isBlank(authorName)) {
            query = addAndToWhere(addAnd, query);
            Author author = authorDao.getAuthorByName(authorName);
            query = query.concat("author_thesis.authorId = " + author.getAuthorId() + " ");
            addAnd = true;
        }
        return query;
    }

    private String filterInstitution(String query, String institution) {
        if (!isBlank(institution)) {
            query = addAndToWhere(addAnd, query);
            query = query.concat("author.university LIKE '%" + institution + "%' ");
            addAnd = true;
        }
        return query;
    }

    private String filterKeyWords(String query, String keyWords) {
        if (!isBlank(keyWords)) {
            query = addAndToWhere(addAnd, query);
            query = query.concat("keywords.keyWords LIKE '%" + keyWords + "%' ");
            addAnd = true;
        }
        return query;
    }

    private String filterQuotationNumber(String query, Integer quotationNumber) {
        if (!isBlank(quotationNumber)) {
            query = addAndToWhere(addAnd, query);
            query = query.concat("thesis.citationNo = " + quotationNumber + " ");
            addAnd = true;
        }
        return query;
    }

    private Boolean isBlank(String filter) {
        return Objects.isNull(filter) || filter.trim().isEmpty();
    }

    private Boolean isBlank(Integer filter) {
        return Objects.isNull(filter) || filter < 0;
    }

    private String addAndToWhere(Boolean addAnd, String query) {
        return (addAnd ? query.concat("AND ") : query);
    }
}
