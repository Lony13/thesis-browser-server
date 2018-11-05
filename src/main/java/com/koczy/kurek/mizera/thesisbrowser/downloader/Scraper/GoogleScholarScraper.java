package com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.HTMLScraper.MOZILLA;
import static com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.HTMLScraper.UTF_8;
import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.SCRAPER_TIMEOUT;
import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.ONLY_NUMBERS;

@Component
public class GoogleScholarScraper {
    private static final Logger logger = Logger.getLogger(GoogleScholarScraper.class.getName());
    private static final String GOOGLE_SCHOLAR_SEARCH_URL = "https://scholar.google.pl/scholar?hl=en&as_sdt=0%2C5&q=";

    public int getCitationNumber(String authorName, String title){
        String searchUrl = getSearchUrl(authorName, title);
        if(searchUrl.equals("")){
            logger.log(Level.WARNING, "Couldn't get url");
            return 0;
        }
        try {
             return Integer.parseInt(Jsoup.connect(searchUrl)
                     .userAgent(MOZILLA)
                     .timeout(SCRAPER_TIMEOUT)
                     .get()
                     .select("a:contains(Cited by)")
                     .first()
                     .text()
                     .replaceAll(ONLY_NUMBERS,""));
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
            logger.log(Level.WARNING, "Couldn't get citation number");
            return 0;
        }
    }

    public List<String> getRelatedTheses(String authorName, String title){
        int pagesNum = getCitationNumber(authorName, title)/10+1;

        ArrayList<String> relatedTheses = new ArrayList<>();
        for(int pageNum=0; pageNum < pagesNum; pageNum++){
            String relatedArticlesPageUrl = getRelatedArticlesUrlFromPage(authorName, title, pageNum);
            relatedTheses.addAll(getRelatedThesesTitles(relatedArticlesPageUrl));
        }

        return relatedTheses;
    }

    private List<String> getRelatedThesesTitles(String relatedArticlesPageUrl) {
        try {
            return Jsoup.connect(relatedArticlesPageUrl)
                    .userAgent(MOZILLA)
                    .timeout(SCRAPER_TIMEOUT)
                    .get()
                    .select("h3 > a")
                    .stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
            logger.log(Level.WARNING, "Couldn't get related theses from " + relatedArticlesPageUrl);
            //return Collections.emptyList();
            return Collections.emptyList();
        }
    }

    private String getRelatedArticlesUrlFromPage(String authorName, String title, int pageNum){
        String searchUrl = getSearchUrl(authorName, title);
        if(searchUrl.equals("")){
            logger.log(Level.WARNING, "Couldn't get url");
            return "";
        }
        try {
            return Jsoup.connect(searchUrl)
                    .userAgent(MOZILLA)
                    .timeout(SCRAPER_TIMEOUT)
                    .get()
                    .select("a:contains(Cited by)")
                    .first()
                    .attr("abs:href")
                    .concat("&start=" + pageNum*10);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.toString());
            logger.log(Level.WARNING, "Couldn't get cited by url");
            return "";
        }
    }

    private String getSearchUrl(String authorName, String title){
        try {
            return GOOGLE_SCHOLAR_SEARCH_URL + URLEncoder.encode(authorName + " " +title, UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.WARNING, e.toString());
            logger.log(Level.WARNING, "Couldn't create url");
            return "";
        }
    }

}
