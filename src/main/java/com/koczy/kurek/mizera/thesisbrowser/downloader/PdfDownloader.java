package com.koczy.kurek.mizera.thesisbrowser.downloader;

import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PdfDownloader {

    private static final Logger log = Logger.getLogger(DownloadService.class.getName());

    private static final String SAVE_DIRECTORY = "downloadedPDF/";

    public PdfDownloader() {
    }

    public InputStream getPdfStream(String urlString) {
        log.info("Opening connection");
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.log(Level.WARNING, e.toString());
        }
        InputStream in = null;
        try {
            in = url.openStream();
        } catch (IOException e) {
            log.log(Level.WARNING, e.toString());
        }
        return in;
    }

    public void downloadPdf(InputStream in, String toFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(SAVE_DIRECTORY + toFileName));
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, e.toString());
        }

        log.info("Reading from resource and writing to file...");
        int length;
        byte[] buffer = new byte[1024];
        try {
            while ((length = in.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, e.toString());
        }
        try {
            fos.close();
            in.close();
        } catch (IOException e) {
            log.log(Level.WARNING, e.toString());
        }
        log.info("File downloaded");
    }
}
