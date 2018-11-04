package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;

@Component
public class GoogleScraper implements HTMLScraper{

    private static final String GOOGLE_SEARCH_URL = "https://google.com/search?q=filetype%3Apdf+";

    public GoogleScraper() {
    }

    @Override
    public String findDownloadPdfLink(String url) {
        String parsedDownloadPdfLink = null;
        try {
            Elements webSitesLinks = Jsoup.connect(url).userAgent(MOZILLA).timeout(SCRAPER_TIMEOUT).
                    get().select(".g>.r>a");

            if (webSitesLinks.isEmpty()) {
                return null;
            }

            String downloadPdfLink = webSitesLinks.get(0).absUrl("href");
            parsedDownloadPdfLink = URLDecoder.decode(downloadPdfLink.substring(downloadPdfLink.indexOf('=') +
                        1, downloadPdfLink.indexOf('&')), UTF_8);

            if (!parsedDownloadPdfLink.startsWith("http")) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedDownloadPdfLink;
    }


    @Override
    public String findUrlToPdf(String pdfName){
        try {
            return GOOGLE_SEARCH_URL + URLEncoder.encode(pdfName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String authorName) {
        return new ArrayList<>();
    }
}
