package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import com.koczy.kurek.mizera.thesisbrowser.downloader.HTTPRequest.HTTPRequest;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
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

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class AGHLibraryScraper implements HTMLScraper{

    private static final String BPP_AGH_URL = "https://bpp.agh.edu.pl/wyszukiwanie/?fA=";
    private static final String TITLE_SEARCH_PREAMBULE = "&fArb=1&fT=";

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

    public HashSet<String> getKeyWords(String authorName, String title){
        String searchUrl = getSearchUrl(authorName, title);
        String pageHTML = getWebsitePageHTML(searchUrl,0);
        String[] keyWords = Jsoup.parse(pageHTML)
                .select(".publ-key")
                .first()
                .text()
                .split(", ");
        keyWords[0] = keyWords[0].split(" ")[1];
        return new HashSet<>(Arrays.asList(keyWords));
    }

    public void setKeyWordsWithTheSameAuthor(List<Thesis> theses, String authorName){
        String searchUrl = getSearchUrl(authorName);
        //TODO
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String firstName, String lastName){
        String url = getSearchUrl(firstName + " " + lastName);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Objects.isNull(doc))
            return new ArrayList<>();

        int pagesNum = getNumberOfPages(doc);

        ArrayList<String> publications = new ArrayList<>();

        for(int pageNum = 1; pageNum <= pagesNum; pageNum++){
            publications.addAll(getPublicationsFromWebsitePage(url, pageNum));
        }
        return publications;
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
                + TITLE_SEARCH_PREAMBULE + title.replaceAll(" ","+");
    }

    private int getNumberOfPages(Document doc){
        Elements e = doc.select(".pagination [title=\"pozycje od-do\"] [type=\"submit\"]");
        return e.size() == 0 ? 1 : e.size()/2;
    }

    private String pageUrlParameters(int pageNumber){
        return "idform=2&vt=p&lastPage=" + pageNumber + "&cur_fOrd=srtAA&page%5B" + pageNumber + "%5D=" + pageNumber;
    }


}
