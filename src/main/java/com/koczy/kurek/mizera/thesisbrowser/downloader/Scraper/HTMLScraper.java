package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

public interface HTMLScraper {

    Logger logger = Logger.getLogger(HTMLScraper.class.getName());

    String MOZILLA = "Mozilla/5.0";
    String UTF_8 = "UTF-8";
    String PDF = "pdf";

    default String findDownloadPdfLink(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).get();
        } catch (IOException e) {
            logger.info("Connection last longer than define timeout: " + SCRAPER_TIMEOUT);
        }
        if(Objects.isNull(doc))
            return null;

        for (Element link : doc.select("a[href]")) {
            String downloadPdfLink = link.attr("abs:href");
            String urlText = link.text();
            if(urlText.toLowerCase().contains(PDF) || downloadPdfLink.toLowerCase().contains(PDF)){
                return downloadPdfLink;
            }
        }
        return null;
    }

    String findUrlToPdf(String pdfName);

    ArrayList<String> getListOfPublicationsByName(String authorName);
}
