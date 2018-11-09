package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.AGHLibraryScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.BagOfWordsConverter;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    private IThesisDao thesisDao;

    @Autowired
    public DownloadService(AGHLibraryScraper aghLibraryScraper,
                           DblpScraper dblpScraper,
                           GoogleScraper googleScraper,
                           PdfDownloader pdfDownloader,
                           PdfParser pdfParser,
                           BagOfWordsConverter bagOfWordsConverter,
                           GoogleScholarScraper googleScholarScraper,
                           IThesisDao thesisDao) {
        this.aghLibraryScraper = aghLibraryScraper;
        this.dblpScraper = dblpScraper;
        this.googleScraper = googleScraper;
        this.pdfDownloader = pdfDownloader;
        this.pdfParser = pdfParser;
        this.bagOfWordsConverter = bagOfWordsConverter;
        this.googleScholarScraper = googleScholarScraper;
        this.thesisDao = thesisDao;
    }

    @Override
    public ResponseEntity downloadTheses(ThesisFilters thesisFilters) {
        Set<Thesis> theses = new HashSet<>();
        if (StringUtils.isEmpty(thesisFilters.getTitle())) {
            theses.addAll(findAllNewThesesFromAuthor(thesisFilters.getAuthor()));
        } else {
            Thesis thesis = findNewThesisByAuthorNameAndTitle(thesisFilters.getAuthor(), thesisFilters.getTitle());
            if(Objects.nonNull(thesis))
                theses.add(thesis);
        }

        for (Thesis thesis : theses) {
            setThesisAttributes(thesisFilters, thesis);
            if (StringUtils.hasText(thesis.getLinkToPDF())) {
                downloadThesis(thesis);
                parseThesisToTxt(thesis);
                parseTxtToBow(thesis);
            } else {
                logger.info("PDF not found");
            }
        }
        //TODO save Thesis and Author to database
        return new ResponseEntity<>("Downloading finished", HttpStatus.OK);
    }

    private void setThesisAttributes(ThesisFilters thesisFilters, Thesis thesis) {
        thesis.setAuthors(aghLibraryScraper.getAuthors(thesisFilters.getAuthor(),
                thesis.getTitle()));
        for(Author author : thesis.getAuthors()){
            author.addThesis(thesis);
        }
        thesis.setCitationNo(googleScholarScraper.getCitationNumber(thesisFilters.getAuthor(),
                thesis.getTitle()));
        thesis.setKeyWords(aghLibraryScraper.getKeyWords(thesisFilters.getAuthor(),
                thesis.getTitle()));
        thesis.setRelatedTheses(googleScholarScraper.getRelatedTheses(thesisFilters.getAuthor(),
                thesis.getTitle()));
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
        thesis.setBow(bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream));
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

    private Thesis findNewThesisByAuthorNameAndTitle(String authorName, String thesisTitle) {
        Thesis thesis = thesisDao.getThesisByTitle(thesisTitle);
        if(Objects.nonNull(thesis)){
            logger.info("Thesis with that title already exists");
            return null;
        }

        String searchTextWithName = authorName + " " + thesisTitle;

        String link = null;
        String url = dblpScraper.findUrlToPdf(thesisTitle);
        if (StringUtils.hasText(url)) {
            link = dblpScraper.findDownloadPdfLink(url);
        } else {
            url = googleScraper.findUrlToPdf(searchTextWithName);
            if (StringUtils.hasText(url)) {
                link = googleScraper.findDownloadPdfLink(url);
            }
        }
        return !StringUtils.isEmpty(link) ? new Thesis(thesisTitle, link) : new Thesis(thesisTitle);
    }

    private Set<Thesis> findAllNewThesesFromAuthor(String authorName) {
        Set<String> publicationsSet = new HashSet<>(aghLibraryScraper.getListOfPublicationsByName(authorName));

        Set<Thesis> theses = new HashSet<>();
        for (String thesisTitle : publicationsSet) {
            Thesis thesis = findNewThesisByAuthorNameAndTitle(authorName, thesisTitle);
            if(Objects.nonNull(thesis))
                theses.add(thesis);
        }
        return theses;
    }
}
