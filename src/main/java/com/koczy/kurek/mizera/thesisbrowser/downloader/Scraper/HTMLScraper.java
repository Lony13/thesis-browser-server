package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public interface HTMLScraper {

    default String findDownloadPdfLink(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

        for (Element link : doc.select("a[href]")) {
            String downloadPdfLink = link.attr("abs:href");
            String urlText = link.text();
            if(urlText.toLowerCase().contains("pdf") || downloadPdfLink.toLowerCase().contains("pdf")){
                return downloadPdfLink;
            }
        }
        return null;
    }

    String findUrlToPdf(String pdfName) throws IOException;

    ArrayList<String> getListOfPublicationsByName(String firstName, String lastName) throws IOException;
}
