package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class BagOfWordsConverter {

    private Map<String, Integer> wordsIdMap = new HashMap<>();

    @Autowired
    public BagOfWordsConverter(@Value("${lda.vocabs}") String vocabsFilePath) {
        try {
            Path vocabFilePath = Paths.get(vocabsFilePath);
            List<String> lines = Files.readAllLines(vocabFilePath);
            for (String line : lines) {
                wordsIdMap.put(line, lines.indexOf(line)+1);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Map<Integer, Integer> convertTxtToBagOfWords(InputStream txtInputStream){
        Map<Integer, Integer> bagOfWords = new HashMap<>();
        Scanner input = new Scanner(txtInputStream);
        while (input.hasNext()) {
            String word = input.next();
            if(wordsIdMap.containsKey(word)){
                if(bagOfWords.containsKey(wordsIdMap.get(word))){
                    bagOfWords.put(wordsIdMap.get(word), bagOfWords.get(wordsIdMap.get(word)) + 1);
                }else{
                    bagOfWords.putIfAbsent(wordsIdMap.get(word), 1);
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : bagOfWords.entrySet())
        {
            if(entry.getValue() <= 3)
                ;
        }
        for (Iterator<Map.Entry<Integer, Integer>> it = bagOfWords.entrySet().iterator(); it.hasNext();) {
            Integer value = it.next().getValue();
            if (value <= 3) {
                it.remove();
            }
        }
        return bagOfWords;
    }
}
