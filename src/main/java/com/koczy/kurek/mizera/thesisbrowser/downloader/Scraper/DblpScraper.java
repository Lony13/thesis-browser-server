package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class DblpScraper implements HTMLScraper {

    private static final String DBLP_UNI_URL = "https://dblp.uni-trier.de/search?q=";
    private static final String REGEX = "[ :/*?|\"<>.]";

    public DblpScraper() {
    }

    @Override
    public String findUrlToPdf(String pdfName){
        Document doc = null;
        try {
            doc = Jsoup.connect(DBLP_UNI_URL
                    + URLEncoder.encode(pdfName, UTF_8)).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Objects.isNull(doc))
            return null;

        for (Element element : doc.select("li.entry")){
            String articleName = element.select(" .title").first().text();
            if(Objects.isNull(articleName))
                return null;

            if(simplifyString(articleName).equals(simplifyString(pdfName))){
                Element link = element.select(" .publ .head > a").first();
                return link.attr("abs:href");
            }
        }
        return null;
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String authorName){
        return new ArrayList<>();
    }

    private String simplifyString(String string){
        return string.replaceAll(REGEX, "").toLowerCase();
    }
}
