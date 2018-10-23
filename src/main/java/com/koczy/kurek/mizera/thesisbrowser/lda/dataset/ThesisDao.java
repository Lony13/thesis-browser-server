package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ThesisDao {

    private List<Map<Integer, Integer>> bow = new ArrayList<Map<Integer, Integer>>();

    public ThesisDao(){
        BagOfWordsConverter bagOfWordsConverter = new BagOfWordsConverter("src/main/resources/vocab.kos.txt");
        FileInputStream fileInputStream = null;
        try {
            for(int i=0; i<1; i++){
                fileInputStream = new FileInputStream("parsedPDF/Multiwinner_Voting__A_New_Challenge_for_Social_Choice_Theory.txt");
                bow.add(bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream));
                fileInputStream = new FileInputStream("parsedPDF/Comparison_of_association_ratio_in_English_and_Polish_languages.txt");
                bow.add(bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream));
                fileInputStream = new FileInputStream("parsedPDF/Predictive_planning_method_for_rescue_robots_in_buildings.txt");
                bow.add(bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getNumDocs(){
        return bow.size();
    }

    public Map<Integer, Integer> getThesisBow(int id){
        return bow.get(id - 1);
    }

}
