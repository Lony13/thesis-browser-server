package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.AGHLibraryScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
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

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.PARSED_PDF_FILE;

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
    private GoogleScholarScraper googleScholarScraper;


    @Autowired
    public DownloadService(AGHLibraryScraper aghLibraryScraper,
                           DblpScraper dblpScraper,
                           GoogleScraper googleScraper,
                           PdfDownloader pdfDownloader,
                           PdfParser pdfParser,
                           BagOfWordsConverter bagOfWordsConverter,
                           GoogleScholarScraper googleScholarScraper) {
        this.aghLibraryScraper = aghLibraryScraper;
        this.dblpScraper = dblpScraper;
        this.googleScraper = googleScraper;
        this.pdfDownloader = pdfDownloader;
        this.pdfParser = pdfParser;
        this.bagOfWordsConverter = bagOfWordsConverter;
        this.googleScholarScraper = googleScholarScraper;
    }

    @Override
    public ResponseEntity downloadTheses(ThesisFilters thesisFilters) {
        Set<Thesis> theses = new HashSet<>();
        if (StringUtils.isEmpty(thesisFilters.getTitle())) {
            theses.addAll(findAllThesesFromAuthor(thesisFilters.getAuthor()));
        } else {
            theses.add(findThesisByAuthorNameAndTitle(thesisFilters.getAuthor(), thesisFilters.getTitle()));
        }

        for (Thesis thesis : theses) {
            thesis.setCitationNo(googleScholarScraper.getCitationNumber(thesisFilters.getAuthor(),
                    thesis.getTitle()));
            thesis.setKeyWords(aghLibraryScraper.getKeyWords(thesisFilters.getAuthor(),
                    thesis.getTitle()));
            thesis.setRelatedTheses(googleScholarScraper.getRelatedTheses(thesisFilters.getAuthor(),
                    thesis.getTitle()));
            if (!StringUtils.isEmpty(thesis.getLinkToPDF())) {
                downloadThesis(thesis);
                parseThesisToTxt(thesis);
                parseTxtToBow(thesis);
            } else {
                logger.info("PDF not found");
            }
        }
        return new ResponseEntity<>("Downloading finished", HttpStatus.OK);
    }

    private void parseTxtToBow(Thesis thesis) {
        String filename = PARSED_PDF_FILE + thesis.getTitle().replaceAll(REGEX, REPLACEMENT) + TXT;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            logger.warning(e.toString());
            logger.warning("Couldn't find file with thesis to parse");
            return;
        }
        if (Objects.isNull(fileInputStream)) {
            logger.warning("File input stream was not initialised");
            return;
        }
        Map<Integer, Integer> thesisBagOfWords = bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream);
        //TODO add thesisBagOfWords to Thesis, save Thesis and Author to database at the end of downloadTheses function
    }

    @Override
    public ResponseEntity updateQuotations(int thesisId) {
        return new ResponseEntity(HttpStatus.OK);
    }

    private void downloadThesis(Thesis thesis) {
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        if (Objects.isNull(in)) {
            logger.warning("File input stream was not initialised");
            return;
        }
        pdfDownloader.downloadPdf(in, filename + PDF);
    }

    private void parseThesisToTxt(Thesis thesis) {
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        if (Objects.isNull(in)) {
            logger.warning("File input stream was not initialised");
            return;
        }
        pdfParser.parseToTxt(in, filename + TXT);
    }

    private Thesis findThesisByAuthorNameAndTitle(String authorName, String thesisTitle) {
        String searchTextWithName = authorName + " " + thesisTitle;

        String link = null;
        String url = dblpScraper.findUrlToPdf(thesisTitle);
        if (!StringUtils.isEmpty(url)) {
            link = dblpScraper.findDownloadPdfLink(url);
        } else {
            url = googleScraper.findUrlToPdf(searchTextWithName);
            if (!StringUtils.isEmpty(url)) {
                link = googleScraper.findDownloadPdfLink(url);
            }
        }
        //TODO add author last and first name \ create proper Thesis
        return !StringUtils.isEmpty(link) ? new Thesis(thesisTitle, authorName, link) : null;
    }


    private Set<Thesis> findAllThesesFromAuthor(String authorName) {
        Set<String> publicationsSet = new HashSet<>(aghLibraryScraper.getListOfPublicationsByName(authorName));

        Set<Thesis> theses = new HashSet<>();
        for (String thesisTitle : publicationsSet) {
            Thesis thesis = findThesisByAuthorNameAndTitle(authorName, thesisTitle);
            if (Objects.nonNull(thesis)) {
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
