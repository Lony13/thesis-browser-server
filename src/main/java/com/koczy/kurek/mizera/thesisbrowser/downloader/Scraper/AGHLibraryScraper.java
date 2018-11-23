package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import com.koczy.kurek.mizera.thesisbrowser.downloader.HTTPRequest.HTTPRequest;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IAuthorDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class AGHLibraryScraper implements HTMLScraper{

    private static final Logger logger = Logger.getLogger(AGHLibraryScraper.class.getName());

    private static final String BPP_AGH_URL = "https://bpp.agh.edu.pl/wyszukiwanie/?fA=";
    private static final String TITLE_SEARCH_PREAMBLE = "&fArb=1&fT=";
    private static final String NO_KEY_WORDS = "brak zdefiniowanych słów kluczowych";

    private HTTPRequest httpRequest;
    private IAuthorDao authorDao;

    @Autowired
    public AGHLibraryScraper(HTTPRequest httpRequest, IAuthorDao authorDao) {
        this.httpRequest = httpRequest;
        this.authorDao = authorDao;
    }

    @Override
    public String findUrlToPdf(String pdfName) {
        //TODO
        return null;
    }

    public Set<String> getKeyWords(String authorName, String title){
        String searchUrl = getSearchUrl(authorName, title);
        String pageHTML = getWebsitePageHTML(searchUrl,0);
        Elements keyWordsGroups = Jsoup.parse(pageHTML)
                .select(".publ-key");
        for(Element keyWordsGroup : keyWordsGroups){
            String[] keyWords = keyWordsGroup
                    .text()
                    .split(", ");
            if(!keyWords[0].equals(NO_KEY_WORDS)){
                keyWords[0] = keyWords[0].split(": ")[1];
                return new HashSet<>(Arrays.asList(keyWords));
            }
        }
        logger.info("Didn't find any key words for " + title);
        return Collections.emptySet();
    }

    public List<Author> getAuthors(String exampleAuthor, String title){
        String searchUrl = getSearchUrl(exampleAuthor, title);
        String pageHTML = getWebsitePageHTML(searchUrl,0);
        ArrayList<String> publicationData = getPublicationData(pageHTML);

        ArrayList<String> authorsNames = new ArrayList<>();
        if(publicationData.size() <= 1){
            authorsNames.add(exampleAuthor);
            logger.warning("Couldn't find Authors for " + title + ", using example author");
        } else {
            authorsNames = new ArrayList<>(Arrays.asList(publicationData.get(1)
                    .split(", ")));
        }
        List<Author> authors = new ArrayList<>();
        for(String authorName : authorsNames){
            Author author = authorDao.getAuthorByName(authorName);
            authors.add(Objects.isNull(author) ? new Author(authorName) : author);
        }
        return authors;
    }

    private ArrayList<String> getPublicationData(String pageHTML) {
        return new ArrayList<>(Arrays.asList(Jsoup.parse(pageHTML)
                            .select(".li-publ .tp1, .tp2, .tp3")
                            .text()
                            .split(" / | // ")));
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String authorName){
        String url = getSearchUrl(authorName);

        Optional<Document> doc = getDocument(url);
        if(!doc.isPresent())
            return new ArrayList<>();

        int pagesNum = getNumberOfPages(doc.get());

        ArrayList<String> publications = new ArrayList<>();
        for(int pageNum = 1; pageNum <= pagesNum; pageNum++){
            publications.addAll(getPublicationsFromWebsitePage(url, pageNum));
        }
        return publications;
    }

    private Optional<Document> getDocument(String url) {
        try {
            return Optional.of(Jsoup.connect(url)
                    .userAgent(MOZILLA)
                    .timeout(SCRAPER_TIMEOUT)
                    .get());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Cannot connect to " + url);
            return Optional.empty();
        }
    }

    private List<String> getPublicationsFromWebsitePage(String url, int pageNum) {
        String pageHTML = getWebsitePageHTML(url, pageNum);
        return publicationListFromHTMLPage(pageHTML);
    }

    private String getWebsitePageHTML(String url, int pageNum) {
        InputStream input = httpRequest.getInputStreamFromPostRequest(url, pageUrlParameters(pageNum));
        String pageHTML = httpRequest.getPageContentFromInputStream(input);
        try {
            input.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Cannot close " + url);
        }
        return pageHTML;
    }

    private ArrayList<String> publicationListFromHTMLPage(String pageHTML) {
        Document doc = Jsoup.parse(pageHTML);
        ArrayList<String> publications = new ArrayList<>();
        for(Element e : doc.select(".li-publ .tp1, .tp2, .tp3")){
            publications.add(e.select("em").first().text());
        }
        return publications;
    }

    private String getSearchUrl(String authorName){
        return BPP_AGH_URL + authorName.replaceAll(" ","+");
    }

    private String getSearchUrl(String authorName, String title){
        return BPP_AGH_URL + authorName.replaceAll(" ","+")
                + TITLE_SEARCH_PREAMBLE + title.replaceAll(" ","+");
    }

    private int getNumberOfPages(Document doc){
        Elements e = doc.select(".pagination [title=\"pozycje od-do\"] [type=\"submit\"]");
        return e.size() == 0 ? 1 : e.size()/2;
    }

    private String pageUrlParameters(int pageNumber){
        return "idform=2&vt=p&lastPage=" + pageNumber + "&cur_fOrd=srtAA&page%5B" + pageNumber + "%5D=" + pageNumber;
    }

}
