package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.downloader.Parser.PdfParser;
import com.koczy.kurek.mizera.thesisbrowser.downloader.PdfDownloader;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.DblpScraper;
import com.koczy.kurek.mizera.thesisbrowser.downloader.Scraper.GoogleScraper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class DownloadService implements IDownloadService {

    @Override
    public ResponseEntity downloadTheses() {
        String searchText = getSearchText();
        searchText = searchText.isEmpty() ?
                "Multiwinner Voting: A New Challenge for Social Choice Theory" : searchText;
        //"Distance rationalization of voting rules" : searchText;

        String filename = searchText.replaceAll("[ :/*?|\"<>.]", "_");


        String url = null;
        DblpScraper dblpScraper = new DblpScraper();
        GoogleScraper googleScraper = new GoogleScraper();

        String link = null;
        try {
            link = dblpScraper.findUrlToPdf(searchText);
            if(link != null){
                url = dblpScraper.findDownloadPdfLink(link);
            } else{
                link = googleScraper.findUrlToPdf(searchText);
                if(link != null){
                    url = googleScraper.findDownloadPdfLink(link);
                }
            }
            if(url != null){
            InputStream in = PdfDownloader.getPdfStream(url);
            PdfParser.parseToTxt(in, filename + ".txt");
            in = PdfDownloader.getPdfStream(url);
            PdfDownloader.downloadPdf(in, filename + ".pdf");
            } else {
                System.out.println("PDF not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    private static String getSearchText() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
