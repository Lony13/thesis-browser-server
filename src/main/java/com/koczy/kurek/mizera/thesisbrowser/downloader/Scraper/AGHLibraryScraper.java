package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import com.koczy.kurek.mizera.thesisbrowser.downloader.HTTPRequest.HTTPRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class AGHLibraryScraper implements HTMLScraper{

    private static final Logger logger = Logger.getLogger(AGHLibraryScraper.class.getName());


    private static final String BPP_AGH_URL = "https://bpp.agh.edu.pl/wyszukiwanie/?fA=";
    private static final String TITLE_SEARCH_PREAMBLE = "&fArb=1&fT=";
    private static final String NO_KEY_WORDS = "brak zdefiniowanych słów kluczowych";

    private HTTPRequest httpRequest;

    @Autowired
    public AGHLibraryScraper() {
        this.httpRequest = new HTTPRequest();
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

    @Override
    public ArrayList<String> getListOfPublicationsByName(String authorName){
        String url = getSearchUrl(authorName);

        Document doc = getDocument(url);
        if(Objects.isNull(doc))
            return new ArrayList<>();

        int pagesNum = getNumberOfPages(doc);

        ArrayList<String> publications = new ArrayList<>();
        for(int pageNum = 1; pageNum <= pagesNum; pageNum++){
            publications.addAll(getPublicationsFromWebsitePage(url, pageNum));
        }
        return publications;
    }

    private Document getDocument(String url) {
        try {
            return Jsoup.connect(url).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
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
