package com.koczy.kurek.mizera.thesisbrowser.schedule;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Objects;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.DAY;

@Component
public class TasksWorker {

    private static final Logger log = Logger.getLogger(TasksWorker.class.getName());
    private ThesisDAO thesisDao;
    private GoogleScholarScraper googleScholarScraper;
    private static final int NEXT_THESIS_NUM = 30;
    private int initialThesisNumber = 0;

    private Calendar cal = Calendar.getInstance();

    @Autowired
    public TasksWorker(ThesisDAO thesisDao,GoogleScholarScraper googleScholarScraper){
        this.thesisDao = thesisDao;
        this.googleScholarScraper = googleScholarScraper;
    }

    @Scheduled(fixedRate = DAY)
    public void updateCitationNumber() {
        log.info("Started updating citation numbers");
        int numOfTheses = thesisDao.getNumTheses();
        int currentThesisNumber = initialThesisNumber;

        while(currentThesisNumber < numOfTheses){
            Thesis currentThesis = thesisDao.getNthThesis(currentThesisNumber);
            if(Objects.nonNull(currentThesis.getTitle())){
                Object[] authors = currentThesis.getAuthors().toArray();
                if(authors.length > 0){
                    Author firstAuthor = (Author) authors[0];
                    int citationNumber = googleScholarScraper.getCitationNumber(firstAuthor.getName(),
                            currentThesis.getTitle());
                    if(citationNumber > currentThesis.getCitationNo()){
                        currentThesis.setCitationNo(citationNumber);
                        thesisDao.saveThesis(currentThesis);
                    }
                }
            }
            currentThesisNumber+=NEXT_THESIS_NUM;
        }
        initialThesisNumber = this.cal.get(Calendar.DAY_OF_MONTH)%NEXT_THESIS_NUM;
        log.info("Citation numbers updated");
    }

}
