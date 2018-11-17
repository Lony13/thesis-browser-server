package com.koczy.kurek.mizera.thesisbrowser.downloader;

import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.PDF_SAVE_DIRECTORY;

@Component
public class PdfDownloader {

    private static final Logger log = Logger.getLogger(DownloadService.class.getName());

    private static final int READ_BUFFER_SIZE = 1024;
    private static final int END_OF_FILE = -1;

    public PdfDownloader() {
    }

    public InputStream getPdfStream(String urlString) {
        log.info("Opening connection to url: " + urlString);
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            in = url.openStream();
        } catch (IOException e) {
            log.log(Level.WARNING, "Could not open stream for url: " + urlString);
        }
        return in;
    }

    public void downloadPdf(InputStream in, String toFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(PDF_SAVE_DIRECTORY + toFileName));
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "Couldn't open output stream to file: " + toFileName);
        }
        if (Objects.isNull(fos)){
            log.log(Level.WARNING, "Couldn't open output stream to file: " + toFileName);
            return;
        }

        log.info("Reading from resource and writing to file: " + toFileName);
        int length;
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        try {
            while ((length = in.read(buffer)) > END_OF_FILE) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "IOException during writing to file: " + toFileName);
        }
        closeStreams(in, fos);
        log.info("File downloaded for file: " + toFileName);
    }

    private void closeStreams(InputStream in, FileOutputStream fos) {
        try {
            fos.close();
            in.close();
        } catch (IOException e) {
            log.log(Level.WARNING, "Exception while closing streams");
        }
    }
}
