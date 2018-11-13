package com.koczy.kurek.mizera.thesisbrowser.schedule;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
                Author firstAuthor = (Author) currentThesis.getAuthors().toArray()[0];
                currentThesis.setCitationNo(googleScholarScraper.getCitationNumber(firstAuthor.getName(),
                        currentThesis.getTitle()));
            }
            currentThesisNumber+=NEXT_THESIS_NUM;
        }

        initialThesisNumber++;
        initialThesisNumber=initialThesisNumber%NEXT_THESIS_NUM;
        log.info("Citation numbers updated");
    }

}
