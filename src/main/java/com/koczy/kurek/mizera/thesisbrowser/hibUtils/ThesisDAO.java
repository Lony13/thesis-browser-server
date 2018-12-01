package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
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

        for (Thesis thesis : thesisList) {
            Hibernate.initialize(thesis.getAuthors());
        }

        transaction.commit();
        session.close();

        thesisList=capitalizeAuthors(thesisList);

        if (thesisList.size() > 0) {
            return thesisList.get(0);
        } else {
            logger.log(Level.INFO,
                    "getThesis | Thesis with id: {0} not found.", thesisId);
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
            logger.log(Level.INFO,
                    "getThesisByTitle | Thesis with title: {0} not found.", title);
            return null;
        }
    }

    @Override
    public Thesis getNthThesis(int n) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String sqlQuery = "SELECT * FROM thesis ORDER BY thesisId LIMIT 1 OFFSET :n";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).setParameter("n", n).list();

        if (thesisList.size() > 0) {
            Hibernate.initialize(thesisList.get(0).getAuthors());
        }
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
    public int getNumThesesWithBow() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String query = "SELECT count(1) FROM thesis WHERE thesisId IN (SELECT DISTINCT bow.thesisId FROM bow)";
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
            logger.log(Level.INFO,
                    "getThesisBow | Thesis with id: {0} not found.", id);
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
    public List<Integer> getThesesIdWithBow() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        String sqlQuery = "SELECT * FROM thesis";
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).list();

        List<Integer> thesesIds = new ArrayList<>();
        for (Thesis thesis : thesisList) {
            if (thesis.getBow().size() > 0) {
                thesesIds.add(thesis.getThesisId());
            }
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
            logger.log(Level.INFO,
                    "saveSimilarityVector | Thesis with id: {0} not found. Save failed.", id);
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
            logger.log(Level.INFO,
                    "getTopicSimilarityVector | Thesis with id: {0} not found.", thesisID);
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

    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {

        if (!anyFilters(thesisFilters)) {
            logger.info("No filters specified. Returning empty list.");
            return new ArrayList<>();
        }

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
            logger.log(Level.INFO, "ThesisFilters are empty");
            thesisList = new ArrayList<>();
        }

        session.close();

        thesisList = filterPosition(thesisFilters, thesisList);

        thesisList = capitalizeAuthors(thesisList);

        return thesisList;
    }

    private List<Thesis> capitalizeAuthors(List<Thesis> thesisList) {
        for(Thesis thesis:thesisList){
            for(Author author:thesis.getAuthors()){
                author.setName(WordUtils.capitalizeFully(author.getName()));
            }
        }
        return thesisList;
    }

    private List<Thesis> filterPosition(ThesisFilters thesisFilters, List<Thesis> thesisList) {
        Author authorFilter = authorDao.getAuthorByName(thesisFilters.getAuthor());
        if (Objects.isNull(authorFilter)) {
            return thesisList;
        }

        ArrayList<Thesis> toRemove = new ArrayList<>();

        String authorName = authorFilter.getName();
        for (Thesis thesis : thesisList) {
            Integer authorIndex = null;

            for (Author author : thesis.getAuthors()) {
                if (Objects.equals(author.getName(), authorName)) {
                    authorIndex = thesis.getAuthors().indexOf(author) + 1;
                    break;
                }
            }

            if (Objects.nonNull(thesisFilters.getPositionTo()) && authorIndex > thesisFilters.getPositionTo()) {
                toRemove.add(thesis);
            }
            if (Objects.nonNull(thesisFilters.getPositionFrom()) && authorIndex < thesisFilters.getPositionFrom()) {
                toRemove.add(thesis);
            }
        }

        thesisList.removeAll(toRemove);

        return thesisList;
    }

    private NativeQuery<Thesis> setAllParameters(NativeQuery<Thesis> nativeQuery, ThesisFilters thesisFilters) {
        if (!isBlank(thesisFilters.getTitle())) {
            nativeQuery.setParameter("title", "%" + thesisFilters.getTitle() + "%");
        }
        if (!isBlank(thesisFilters.getAuthor())) {
            nativeQuery.setParameter("authorName", "%" + thesisFilters.getAuthor() + "%");
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

    private String createQuery(ThesisFilters filters) {

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
                (!isBlank(filters.getAuthor()) && !isBlank(filters.getPositionFrom())) ||
                (!isBlank(filters.getAuthor()) && !isBlank(filters.getPositionTo())) ||
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

    private String filterAuthor(String query, String authorName) {
        if (!isBlank(authorName)) {
            query = addAndToWhere(query);
            query = query.concat("author_thesis.authorId IN (SELECT authorId FROM author WHERE name LIKE :authorName ) ");
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
