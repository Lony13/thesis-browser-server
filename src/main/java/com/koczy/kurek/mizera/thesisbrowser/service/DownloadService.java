package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.AGHLibraryScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DownloadService implements IDownloadService {

    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());

    private static final String DEFAULT_THESIS = "Multiwinner Voting: A New Challenge for Social Choice Theory";
    private static final String REGEX = "[ :/*?|\"<>.]";
    private static final String REPLACEMENT = "_";
    private static final String TXT = ".txt";
    private static final String PDF = ".pdf";

    private AGHLibraryScraper aghLibraryScraper;
    private DblpScraper dblpScraper;
    private GoogleScraper googleScraper;
    private PdfDownloader pdfDownloader;
    private PdfParser pdfParser;

    @Autowired
    public DownloadService(AGHLibraryScraper aghLibraryScraper,
                           DblpScraper dblpScraper,
                           GoogleScraper googleScraper,
                           PdfDownloader pdfDownloader,
                           PdfParser pdfParser) {
        this.aghLibraryScraper = aghLibraryScraper;
        this.dblpScraper = dblpScraper;
        this.googleScraper = googleScraper;
        this.pdfDownloader = pdfDownloader;
        this.pdfParser = pdfParser;
    }

    @Override
    public ResponseEntity downloadTheses(ThesisFilters thesisFilters) {
        String searchText = getSearchText();
        searchText = StringUtils.isEmpty(searchText) ? DEFAULT_THESIS : searchText;
        String filename = searchText.replaceAll(REGEX, REPLACEMENT);

        String url = null;
        String link;
        try {
            link = dblpScraper.findUrlToPdf(searchText);

            if(!StringUtils.isEmpty(link)){
                url = dblpScraper.findDownloadPdfLink(link);
            } else {
                link = googleScraper.findUrlToPdf(searchText);
                if(!StringUtils.isEmpty(link)){
                    url = googleScraper.findDownloadPdfLink(link);
                }
            }

            if(!StringUtils.isEmpty(url)) {
                InputStream in = pdfDownloader.getPdfStream(url);
                pdfParser.parseToTxt(in, filename + TXT);
                in = pdfDownloader.getPdfStream(url);
                pdfDownloader.downloadPdf(in, filename + PDF);
            } else {
                logger.info("PDF not found");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    private Thesis findThesisByAuthorNameAndTitle(String firstName, String lastName, String thesisTitle) throws IOException {
        String authorName = firstName + " " + lastName;
        String searchTextWithName = authorName + " " + thesisTitle;

        String link = null;
        String url = dblpScraper.findUrlToPdf(thesisTitle);
        if(url != null){
            link = dblpScraper.findDownloadPdfLink(url);
        } else{
            url = googleScraper.findUrlToPdf(searchTextWithName);
            if(url != null){
                link = googleScraper.findDownloadPdfLink(url);
            }
        }

        if(link != null){
            return new Thesis(thesisTitle, authorName, link);
        } else {
            return null;
        }
    }


    private Set<Thesis> findAllThesesFromAuthor(String firstName, String lastName) throws IOException {
        Set<String> publicationsSet = new HashSet<>(aghLibraryScraper.getListOfPublicationsByName(firstName, lastName));

        Set<Thesis> theses = new HashSet<>();
        for(String thesisTitle : publicationsSet){
            Thesis thesis = findThesisByAuthorNameAndTitle(firstName, lastName, thesisTitle);
            if(thesis != null){
                theses.add(thesis);
            }
        }
        return theses;
    }

    private String getSearchText() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.toString());
        }
        return null;
    }
}
