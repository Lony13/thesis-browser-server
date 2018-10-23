package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.AGHLibraryScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.BagOfWordsConverter;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DownloadService implements IDownloadService {

    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());

    private static final String REGEX = "[ :/*?|\"<>.]";
    private static final String REPLACEMENT = "_";
    private static final String TXT = ".txt";
    private static final String PDF = ".pdf";

    private AGHLibraryScraper aghLibraryScraper;
    private DblpScraper dblpScraper;
    private GoogleScraper googleScraper;
    private PdfDownloader pdfDownloader;
    private PdfParser pdfParser;
    private BagOfWordsConverter bagOfWordsConverter;

    @Autowired
    public DownloadService(AGHLibraryScraper aghLibraryScraper,
                           DblpScraper dblpScraper,
                           GoogleScraper googleScraper,
                           PdfDownloader pdfDownloader,
                           PdfParser pdfParser,
                           BagOfWordsConverter bagOfWordsConverter) {
        this.aghLibraryScraper = aghLibraryScraper;
        this.dblpScraper = dblpScraper;
        this.googleScraper = googleScraper;
        this.pdfDownloader = pdfDownloader;
        this.pdfParser = pdfParser;
        this.bagOfWordsConverter = bagOfWordsConverter;
    }

    @Override
    public ResponseEntity downloadTheses(ThesisFilters thesisFilters) {
        String firstName = thesisFilters.getAuthor().split(" ")[0];
        String lastName = thesisFilters.getAuthor().split(" ")[1];
        Set<Thesis> theses = new HashSet<>();
        if(StringUtils.isEmpty(thesisFilters.getTitle())){
            theses.addAll(findAllThesesFromAuthor(firstName, lastName));
        }else{
            theses.add(findThesisByAuthorNameAndTitle(firstName, lastName, thesisFilters.getTitle()));
        }

        for (Thesis thesis : theses) {
            if(!StringUtils.isEmpty(thesis.getLinkToPDF())){
                downloadThesis(thesis);
                parseThesisToTxt(thesis);
                parseTxtToBow(thesis);
            } else {
                logger.info("PDF not found");
            }
        }
        return new ResponseEntity<>("Downloading finished", HttpStatus.OK);
    }

    private void parseTxtToBow(Thesis thesis){
        String filename = "parsedPDF/"+thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<Integer, Integer> thesisBagOfWords = bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream);
        //TODO add thesisBagOfWords to Thesis, save Thesis and Author to database
    }

    private void downloadThesis(Thesis thesis){
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        pdfDownloader.downloadPdf(in, filename + PDF);
    }

    private void parseThesisToTxt(Thesis thesis){
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        pdfParser.parseToTxt(in, filename + TXT);
    }

    private Thesis findThesisByAuthorNameAndTitle(String firstName, String lastName, String thesisTitle){
        String authorName = firstName + " " + lastName;
        String searchTextWithName = authorName + " " + thesisTitle;

        String link = null;
        String url = dblpScraper.findUrlToPdf(thesisTitle);
        if(!StringUtils.isEmpty(url)){
            link = dblpScraper.findDownloadPdfLink(url);
        } else {
            url = googleScraper.findUrlToPdf(searchTextWithName);
            if(!StringUtils.isEmpty(url)){
                link = googleScraper.findDownloadPdfLink(url);
            }
        }
        //TODO add author last and first name \ create proper Thesis
        return !StringUtils.isEmpty(link) ? new Thesis(thesisTitle, authorName, link) : null;
    }


    private Set<Thesis> findAllThesesFromAuthor(String firstName, String lastName){
        Set<String> publicationsSet = new HashSet<>(aghLibraryScraper.getListOfPublicationsByName(firstName, lastName));

        Set<Thesis> theses = new HashSet<>();
        for(String thesisTitle : publicationsSet){
            Thesis thesis = findThesisByAuthorNameAndTitle(firstName, lastName, thesisTitle);
            if(Objects.nonNull(thesis)){
                theses.add(thesis);
            }
        }
        return theses;
    }

    private String getSearchText() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }
        return null;
    }
}
