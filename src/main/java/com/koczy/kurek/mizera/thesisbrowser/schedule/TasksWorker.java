package com.koczy.kurek.mizera.thesisbrowser.schedule;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.DAY;

@Component
public class TasksWorker {

    private static final Logger log = Logger.getLogger(TasksWorker.class.getName());
    private ThesisDAO thesisDao;
    private GoogleScholarScraper googleScholarScraper;
    private static final int NEXT_THESIS_NUM = 30;
    int initialThesisNumber = 0;

    @Autowired
    public TasksWorker(ThesisDAO thesisDao,GoogleScholarScraper googleScholarScraper){
        this.thesisDao = thesisDao;
        this.googleScholarScraper = googleScholarScraper;
    }

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("Worker: The time is now " + new Date());
    }

    @Scheduled(fixedRate = DAY)
    public void updateCitationNumber() {
        log.info("Started updating citation numbers");
        int numOfTheses = thesisDao.getNumTheses();
        int currentThesisNumber = initialThesisNumber;

        while(currentThesisNumber < numOfTheses){
            Thesis currentThesis = thesisDao.getNthThesis(currentThesisNumber);
            currentThesis.setCitationNo(googleScholarScraper.getCitationNumber(currentThesis.getAuthor(),
                    currentThesis.getTitle()));
            currentThesisNumber+=NEXT_THESIS_NUM;
        }

        initialThesisNumber++;
        initialThesisNumber=initialThesisNumber%NEXT_THESIS_NUM;
        log.info("Citation numbers updated");
    }

}
