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
import java.util.ArrayList;
import java.util.Objects;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class AGHLibraryScraper implements HTMLScraper{

    private static final String BPP_AGH_URL = "https://bpp.agh.edu.pl/wyszukiwanie/?fA=";

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

    @Override
    public ArrayList<String> getListOfPublicationsByName(String firstName, String lastName){
        String url = createUrl(firstName, lastName);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Objects.isNull(doc))
            return new ArrayList<>();

        int pageNumber = getNumberOfPages(doc);

        ArrayList<String> publications = new ArrayList<>();

        for(int i = 1; i <= pageNumber; i++){
            InputStream input = httpRequest.getInputStreamFromPostRequest(url, pageUrlParameters(i));
            String pageHTML = httpRequest.getPageContentFromInputStream(input);
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publications.addAll(publicationListFromOnePage(pageHTML));
        }
        return publications;
    }

    private ArrayList<String> publicationListFromOnePage(String pageHTML) {
        Document doc = Jsoup.parse(pageHTML);
        ArrayList<String> publications = new ArrayList<>();
        for(Element e : doc.select(".li-publ .tp1, .tp2, .tp3")){
            publications.add(e.select("em").first().text());
        }
        return publications;
    }

    private String createUrl(String firstName, String lastName){
        return BPP_AGH_URL + firstName + "+" + lastName;
    }

    private int getNumberOfPages(Document doc){
        Elements e = doc.select(".pagination [title=\"pozycje od-do\"] [type=\"submit\"]");
        return e.size() == 0 ? 1 : e.size()/2;
    }

    private String pageUrlParameters(int pageNumber){
        return "idform=2&vt=p&lastPage=" + pageNumber + "&cur_fOrd=srtAA&page%5B" + pageNumber + "%5D=" + pageNumber;
    }


}
