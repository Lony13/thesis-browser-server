package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.AGHLibraryScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScholarScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScraper;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IAuthorDao;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.BagOfWordsConverter;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.PARSED_PDF_FILE;
import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.PDF_SAVE_DIRECTORY;

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
    private IAuthorDao authorDao;

    @Autowired
    public DownloadService(AGHLibraryScraper aghLibraryScraper,
                           DblpScraper dblpScraper,
                           GoogleScraper googleScraper,
                           PdfDownloader pdfDownloader,
                           PdfParser pdfParser,
                           BagOfWordsConverter bagOfWordsConverter,
                           GoogleScholarScraper googleScholarScraper,
                           IThesisDao thesisDao,
                           IAuthorDao authorDao) {
        this.aghLibraryScraper = aghLibraryScraper;
        this.dblpScraper = dblpScraper;
        this.googleScraper = googleScraper;
        this.pdfDownloader = pdfDownloader;
        this.pdfParser = pdfParser;
        this.bagOfWordsConverter = bagOfWordsConverter;
        this.googleScholarScraper = googleScholarScraper;
        this.thesisDao = thesisDao;
        this.authorDao = authorDao;
    }

    @Override
    public ResponseEntity<ServerInfo> downloadTheses(ThesisFilters thesisFilters) {
        createDir(PARSED_PDF_FILE);
        createDir(PDF_SAVE_DIRECTORY);
        Set<Thesis> theses = new HashSet<>();
        if (StringUtils.isEmpty(thesisFilters.getTitle())) {
            theses.addAll(findAllNewThesesFromAuthor(thesisFilters.getAuthor()));
        } else {
            Thesis thesis = findNewThesisByAuthorNameAndTitle(thesisFilters.getAuthor(), thesisFilters.getTitle());
            if (Objects.nonNull(thesis))
                theses.add(thesis);
        }

        if(theses.size() <= 0){
            return new ResponseEntity<>(new ServerInfo(new Date(),
                    "Couldn't find given papers"), HttpStatus.NOT_FOUND);
        }

        int parsedTheses = 0;
        for (Thesis thesis : theses) {
            setThesisAttributes(thesisFilters, thesis);
            if (StringUtils.hasText(thesis.getLinkToPDF())) {
                downloadThesis(thesis);
                parseThesisToTxt(thesis);
                if(parseTxtToBow(thesis)){
                    parsedTheses++;
                }

            } else {
                logger.info("PDF not found for given thesisFilters");
            }
            for (Author author : thesis.getAuthors()) {
                authorDao.saveAuthor(author);
            }
        }
        deleteDir(PARSED_PDF_FILE);
        deleteDir(PDF_SAVE_DIRECTORY);

        if (StringUtils.isEmpty(thesisFilters.getTitle())){
            return new ResponseEntity<>(new ServerInfo(new Date(),
                    "Found " + theses.size() + " papers for " + thesisFilters.getAuthor()
                            + ", downloaded: " + parsedTheses), HttpStatus.OK);
        }
        else {
            if(parsedTheses == 0){
                return new ResponseEntity<>(new ServerInfo(new Date(),
                        "Found paper: " + thesisFilters.getTitle() + ", couldn't download it"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ServerInfo(new Date(),
                        "Found and downloaded paper: " + thesisFilters.getTitle()), HttpStatus.OK);
            }
        }
    }

    private void deleteDir(String name) {
        try {
            FileUtils.deleteDirectory(new File(name));
            logger.info("Deleted directory : " + name);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while deleting folder : " + name);
        }
    }

    private void createDir(String name) {
        File file = new File(name);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void setThesisAttributes(ThesisFilters thesisFilters, Thesis thesis) {
        thesis.setAuthors(aghLibraryScraper.getAuthors(thesisFilters.getAuthor(),
                thesis.getTitle()));
        for (Author author : thesis.getAuthors()) {
            author.addThesis(thesis);
        }
        thesis.setCitationNo(googleScholarScraper.getCitationNumber(thesisFilters.getAuthor(),
                thesis.getTitle()));
        thesis.setKeyWords(aghLibraryScraper.getKeyWords(thesisFilters.getAuthor(),
                thesis.getTitle()));
        thesis.setRelatedTheses(googleScholarScraper.getRelatedTheses(thesisFilters.getAuthor(),
                thesis.getTitle()));
        thesis.setPublicationDate(googleScholarScraper.getPublicationDate(thesisFilters.getAuthor(),
                thesis.getTitle()));
    }

    private boolean parseTxtToBow(Thesis thesis) {
        String filename = PARSED_PDF_FILE + thesis.getTitle().replaceAll(REGEX, REPLACEMENT) + TXT;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            logger.warning("Couldn't find file with thesis to parse, thesis title: " + thesis.getTitle());
            return false;
        } catch (NullPointerException e) {
            logger.warning("Couldn't find file with thesis to parse");
            return false;
        }
        thesis.setBow(bagOfWordsConverter.convertTxtToBagOfWords(fileInputStream));
        closeStream(fileInputStream);
        return true;
    }

    @Override
    public ResponseEntity<ServerInfo> updateQuotations(int thesisId) {
        Thesis thesis = thesisDao.getThesis(thesisId);
        if(Objects.isNull(thesis) || thesis.getAuthors().size() <= 0){
            return new ResponseEntity<>(new ServerInfo(new Date(),
                    "Couldn't update quotations for thesis with id: "+ thesisId), HttpStatus.NOT_FOUND);
        }
        Author firstAuthor = (Author)thesis.getAuthors().toArray()[0];
        thesis.setCitationNo(googleScholarScraper.getCitationNumber(firstAuthor.getName(),
                thesis.getTitle()));
        thesis.setRelatedTheses(googleScholarScraper.getRelatedTheses(firstAuthor.getName(),
                thesis.getTitle()));
        thesisDao.saveThesis(thesis);
        return new ResponseEntity<>(new ServerInfo(new Date(),
                "Updated quotations for papers: " + thesis.getTitle()), HttpStatus.OK);
    }

    private void downloadThesis(Thesis thesis) {
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        if (Objects.isNull(in)) {
            logger.warning("File input stream was not initialised");
            return;
        }
        pdfDownloader.downloadPdf(in, filename + PDF);
        closeStream(in);
    }

    private void parseThesisToTxt(Thesis thesis) {
        String filename = thesis.getTitle().replaceAll(REGEX, REPLACEMENT);
        InputStream in = pdfDownloader.getPdfStream(thesis.getLinkToPDF());
        if (Objects.isNull(in)) {
            logger.warning("File input stream was not initialised");
            return;
        }
        pdfParser.parseToTxt(in, filename + TXT);
        closeStream(in);
    }

    private Thesis findNewThesisByAuthorNameAndTitle(String authorName, String thesisTitle) {
        Thesis thesis = thesisDao.getThesisByTitle(thesisTitle);
        if (Objects.nonNull(thesis)) {
            logger.info("Thesis with title: " + thesisTitle + " already exists");
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
            if (Objects.nonNull(thesis))
                theses.add(thesis);
        }
        return theses;
    }

    private void closeStream(InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't close stream: " + stream);
        }
    }
}
