package com.koczy.kurek.mizera.thesisbrowser.downloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class PdfDownloader {
    private static final String saveDirectory = "downloadedPDF/";

    public static InputStream getPdfStream(String urlString) {
        System.out.println("opening connection");
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        try {
            in = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    public static void downloadPdf(InputStream in, String toFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(saveDirectory + toFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("reading from resource and writing to file...");
        int length;
        byte[] buffer = new byte[1024];// buffer for portion of data from connection
        try {
            while ((length = in.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File downloaded");
    }
}
