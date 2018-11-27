package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;

import java.util.List;
import java.util.Map;

public interface IThesisDao {

    List<Thesis> searchTheses(ThesisFilters thesisFilters);

    Thesis getThesis(int thesisId);

    Thesis getThesisByTitle(String title);

    Thesis getNthThesis(int n);

    int getNumTheses();

    Map<Integer, Integer> getThesisBow(int id);

    List<Integer> getThesesId();

    void saveThesis(Thesis thesis);

    void saveSimilarityVector(Integer id, double[] similarityVector);

    double[] getTopicSimilarityVector(int thesisID);

    double[] convertToPrimitives(List<Double> similarityVector);

    List<Integer> getThesesIdWithBow();

    int getNumThesesWithBow();

}
