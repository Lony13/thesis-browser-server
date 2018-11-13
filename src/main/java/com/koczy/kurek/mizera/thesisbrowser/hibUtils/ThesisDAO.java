package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.exceptions.NoAuthorException;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.*;
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

    @Override
    public Thesis getThesis(int thesisId) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String sqlQuery = "SELECT * FROM thesis WHERE thesisId =:thesisId";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).setParameter("thesisId", thesisId).list();

        transaction.commit();
        session.close();

        if (thesisList.size() > 0) {
            return thesisList.get(0);
        } else {
            logger.log(Level.SEVERE,
                    "ThesisDAO.getThesis | Thesis not found. Returning null.");
            return null;
        }
    }

    @Override
    public Thesis getThesisByTitle(String title) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String sqlQuery = "SELECT * FROM thesis WHERE title =:title";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).setParameter("title", title).list();

        transaction.commit();
        session.close();

        if (thesisList.size() > 0) {
            return thesisList.get(0);
        } else {
            logger.log(Level.SEVERE,
                    "ThesisDAO.getThesisByTitle | Thesis not found. Returning null.");
            return null;
        }
    }

    @Override
    public Thesis getNthThesis(int n) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String sqlQuery = "SELECT * FROM thesis ORDER BY thesisId LIMIT 1 OFFSET :n";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).setParameter("n", n).list();

        session.close();

        return thesisList.size() > 0 ? thesisList.get(0) : null;
    }

    @Override
    public int getNumTheses() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String query = "SELECT count(1) FROM thesis";
        int result = ((BigInteger) session.createNativeQuery(query).uniqueResult()).intValue();
        session.close();

        return result;
    }

    @Override
    public Map<Integer, Integer> getThesisBow(int id) {
        Map<Integer, Integer> result = new HashMap<>();
        if (Objects.nonNull(getThesis(id))) {
            result = getThesis(id).getBow();
        } else {
            logger.log(Level.SEVERE,
                    "ThesisDAO.getThesisBow() | thesis not found. Returning empty Map.");
        }
        return result;
    }

    @Override
    public List<Integer> getThesesId() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String sqlQuery = "SELECT * FROM thesis";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).list();

        List<Integer> thesesIds = new ArrayList<>();
        for (Thesis thesis : thesisList) {
            thesesIds.add(thesis.getThesisId());
        }

        session.close();
        return thesesIds;
    }

    @Override
    public void saveThesis(Thesis thesis) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.saveOrUpdate(thesis);

        transaction.commit();
        session.close();
    }

    @Override
    public void saveSimilarityVector(Integer id, double[] similarityVector) {
        Thesis thesis = getThesis(id);
        if (Objects.isNull(thesis)) {
            logger.log(Level.SEVERE,
                    "ThesisDAO.saveSimilarityVector() | thesis not found. Save failed.");
            return;
        }

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Double[] tmpDoubleArray = ArrayUtils.toObject(similarityVector);
        thesis.setSimilarityVector(Arrays.asList(tmpDoubleArray));
        session.saveOrUpdate(thesis);

        transaction.commit();
        session.close();
    }

    @Override
    public double[] getTopicSimilarityVector(int thesisID) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Thesis thesis = session.get(Thesis.class, thesisID);
        if (Objects.isNull(thesis)) {
            transaction.commit();
            session.close();
            logger.log(Level.SEVERE,
                    "ThesisDAO.getTopicSimilarityVector() | thesis not found. Returning new double[0]");
            return new double[0];
        }

        Hibernate.initialize(thesis.getSimilarityVector());
        transaction.commit();
        session.close();

        return convertToPrimitives(thesis.getSimilarityVector());
    }

    @Override
    public double[] convertToPrimitives(List<Double> similarityVector) {
        List<Double> doubles = similarityVector;
        double[] result = new double[doubles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = doubles.get(i);
        }
        return result;
    }

    //TODO add filter over position in authors list
    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Thesis> thesisList;

        try {

            String sqlQuery = createQuery(thesisFilters);
            NativeQuery<Thesis> nativeQuery = session.createNativeQuery(sqlQuery, Thesis.class);
            nativeQuery = setAllParameters(nativeQuery, thesisFilters);
            thesisList = nativeQuery.list();

            for (Thesis thesis : thesisList) {
                Hibernate.initialize(thesis.getRelatedTheses());
                Hibernate.initialize(thesis.getKeyWords());
                Hibernate.initialize(thesis.getAuthors());
                Hibernate.initialize(thesis.getSimilarityVector());
                Hibernate.initialize(thesis.getBow());
            }
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "NullPointerException in thesisFilers. Returning empty list." + e.toString());
            thesisList = new ArrayList<>();
        } catch (NoAuthorException e) {
            logger.log(Level.SEVERE, "Couldn't find author. Returning empty list.");
            thesisList = new ArrayList<>();
        }

        session.close();

        return thesisList;
    }

    private NativeQuery<Thesis> setAllParameters(NativeQuery<Thesis> nativeQuery, ThesisFilters thesisFilters) {
        if (!isBlank(thesisFilters.getTitle())) {
            nativeQuery.setParameter("title", "%" + thesisFilters.getTitle() + "%");
        }
        if (!isBlank(thesisFilters.getInstitution())) {
            nativeQuery.setParameter("institution", "%" + thesisFilters.getInstitution() + "%");
        }
        if (!isBlank(thesisFilters.getKeyWords())) {
            nativeQuery.setParameter("keyWords", "%" + thesisFilters.getKeyWords() + "%");
        }
        if (!isBlank(thesisFilters.getQuotationNumber())) {
            nativeQuery.setParameter("quotationNumber", thesisFilters.getQuotationNumber());
        }
        if (!isBlank(thesisFilters.getDateFrom())) {
            nativeQuery.setParameter("dateFrom", thesisFilters.getDateFrom());
        }
        if (!isBlank(thesisFilters.getDateTo())) {
            nativeQuery.setParameter("dateTo", thesisFilters.getDateTo());
        }
        return nativeQuery;
    }

    private String createQuery(ThesisFilters filters) throws NoAuthorException {

        if (!anyFilters(filters)) {
            logger.info("No filters specified. Selecting random 10 theses.");
            return "SELECT * FROM thesis LIMIT 10";
        }

        String query = "SELECT * FROM thesis " +
                "LEFT JOIN author_thesis on thesis.thesisId = author_thesis.thesisId " +
                "LEFT JOIN author on author_thesis.authorId = author.authorId " +
                "LEFT JOIN keywords on keywords.thesisId = thesis.thesisId " +
                "WHERE ";

        addAnd = false;
        query = filterTitle(query, filters.getTitle());
        query = filterAuthor(query, filters.getAuthor());
        query = filterInstitution(query, filters.getInstitution());
        query = filterKeyWords(query, filters.getKeyWords());
        query = filterQuotationNumber(query, filters.getQuotationNumber());
        query = filterDateFrom(query, filters.getDateFrom());
        query = filterDateTo(query, filters.getDateTo());

        query = query.concat("GROUP BY thesis.thesisId");
        return query;
    }

    private boolean anyFilters(ThesisFilters filters) {
        return !isBlank(filters.getTitle()) ||
                !isBlank(filters.getAuthor()) ||
                !isBlank(filters.getInstitution()) ||
                !isBlank(filters.getKeyWords()) ||
                !isBlank(filters.getDateFrom()) ||
                !isBlank(filters.getDateTo()) ||
                !isBlank(filters.getQuotationNumber());
    }

    private String filterTitle(String query, String title) {
        if (!isBlank(title)) {
            query = addAndToWhere(query);
            query = query.concat("thesis.title LIKE :title ");
            addAnd = true;
        }
        return query;
    }

    private String filterAuthor(String query, String authorName) throws NoAuthorException {
        if (!isBlank(authorName)) {
            Author author = authorDao.getAuthorByName(authorName);
            if (Objects.isNull(author)) {
                throw new NoAuthorException();
            }

            query = addAndToWhere(query);
            query = query.concat("author_thesis.authorId = " + author.getAuthorId() + " ");
            addAnd = true;
        }
        return query;
    }

    private String filterInstitution(String query, String institution) {
        if (!isBlank(institution)) {
            query = addAndToWhere(query);
            query = query.concat("author.university LIKE :institution ");
            addAnd = true;
        }
        return query;
    }

    private String filterKeyWords(String query, String keyWords) {
        if (!isBlank(keyWords)) {
            query = addAndToWhere(query);
            query = query.concat("keywords.keyWords LIKE :keyWords ");
            addAnd = true;
        }
        return query;
    }

    private String filterQuotationNumber(String query, Integer quotationNumber) {
        if (!isBlank(quotationNumber)) {
            query = addAndToWhere(query);
            query = query.concat("thesis.citationNo = :quotationNumber ");
            addAnd = true;
        }
        return query;
    }

    private String filterDateFrom(String query, Integer dateFrom) {
        if (!isBlank(dateFrom)) {
            query = addAndToWhere(query);
            query = query.concat("thesis.publicationDate >= :dateFrom ");
            addAnd = true;
        }
        return query;
    }

    private String filterDateTo(String query, Integer dateTo) {
        if (!isBlank(dateTo)) {
            query = addAndToWhere(query);
            query = query.concat("thesis.publicationDate <= :dateTo ");
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

    private String addAndToWhere(String query) {
        return (addAnd ? query.concat("AND ") : query);
    }

}
