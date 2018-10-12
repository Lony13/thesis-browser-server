package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

@Component
public class GoogleScraper implements HTMLScraper{

    public GoogleScraper() {
    }

    @Override
    public String findDownloadPdfLink(String url) {
        String parsedDownloadPdfLink = null;
        try {
            Elements webSitesLinks = Jsoup.connect(url).userAgent("Mozilla/5.0").
                    get().select(".g>.r>a");

            if (webSitesLinks.isEmpty()) {
                return null;
            }

            String downloadPdfLink = webSitesLinks.get(0).absUrl("href");
            try {
                parsedDownloadPdfLink = URLDecoder.decode(downloadPdfLink.substring(downloadPdfLink.indexOf('=') +
                        1, downloadPdfLink.indexOf('&')), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (!parsedDownloadPdfLink.startsWith("http")) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return parsedDownloadPdfLink;
    }


    @Override
    public String findUrlToPdf(String pdfName) throws IOException {
        String encoding = "UTF-8";
        return "https://google.com/search?q=filetype%3Apdf+"
                + URLEncoder.encode(pdfName, encoding);
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String firstName, String lastName) throws IOException {
        return new ArrayList<>();
    }
}
