package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DblpScraper implements HTMLScraper {

    @Override
    public String findUrlToPdf(String pdfName) throws IOException {
        Document doc = Jsoup.connect("https://dblp.uni-trier.de/search?q="
                + URLEncoder.encode(pdfName, "UTF-8")).userAgent("Mozilla/5.0").get();

        for (Element element : doc.select("li.entry")){
            String articleName = element.select(" .title").first().text();

            if(simplifyString(articleName).equals(simplifyString(pdfName))){
                Element link = element.select(" .publ .head > a").first();
                return link.attr("abs:href");
            }
        }
        return null;
    }

    @Override
    public ArrayList<String> getListOfPublicationsByName(String firstName, String lastName) throws IOException {
        return new ArrayList<>();
    }

    private static String simplifyString(String string){
        return string.replaceAll("[ :/*?|\"<>.]", "").toLowerCase();
    }
}
